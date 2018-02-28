/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aika.corpus;


import org.aika.lattice.NodeActivation;
import org.aika.lattice.NodeActivation.Key;
import org.aika.neuron.Activation;
import org.aika.Utils;
import org.aika.corpus.SearchNode.Decision;

import java.util.*;


/**
 * The {@code InterpretationNode} class represents a node within the interpretations lattice. Such a node consists of a conjunction of
 * primitive interpretation nodes that are emitted during each activation of a {@code Neuron} (but not the InputNeurons).
 * The primitive nodes them self can consist of a disjunction of other interpretation nodes.
 * For example the primitive interpretation node 9[(1),(6,7,8)] has the primitive node id 9 and consists of the
 * disjunction of the interpretation nodes (1) and (6,7,8). These conjunctions and disjunction in the interpretation
 * representation is necessary since the logic layer underneath each neuron too consists of conjunctions and disjunctions.
 *
 * <p>There may be conflicts between interpretation nodes in the lattice. Conflicts are generated by negative recurrent
 * synapses.
 *
 * @author Lukas Molzberger
 */
public class InterpretationNode implements Comparable<InterpretationNode> {

    public static final InterpretationNode MIN = new InterpretationNode(null, -1, 0, 0);
    public static final InterpretationNode MAX = new InterpretationNode(null, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);

    public static int MAX_SELF_REFERENCING_DEPTH = 5;

    public final int primId;
    public int minPrim = -1;
    public int maxPrim = -1;

    public final int id;
    public int length;

    public Set<InterpretationNode> orInterpretationNodes;
    public Set<InterpretationNode> selectedOrInterpretationNodes;
    public Set<InterpretationNode> refByOrInterpretationNode;
    public InterpretationNode largestCommonSubset;
    public Set<InterpretationNode> linkedByLCS;

    /*
    Aika extensively uses graph coloring techniques. When traversing the interpretation node lattice nodes will be
    marked in order to avoid having to visit the same node twice. To avoid having to reset each mark Aika uses the
    counter {@code Document.interpretationIdCounter} to set a new mark each time.
     */
    private long visitedLinkRelations;
    private long visitedContains;
    private long visitedCollect;
    private long visitedIsConflicting;
    private long visitedComputeLargestCommonSubset;
    private long visitedComputeLength;
    private long visitedComputeParents;
    private long visitedNumberInnerInputs;
    private long visitedComputeChildren;
    private long visitedGetState;
    private long visitedChangeSelected;

    public long markedConflict;

    private int numberInnerInputs = 0;
    private int largestCommonSubsetCount = 0;
    private int numberOfInputsComputeChildren = 0;

    long visitedState;
    public Decision state = Decision.UNKNOWN;
    public Decision finalState = Decision.UNKNOWN;
    public boolean fixed;

    public boolean isSelected;

    public final Document doc;
    public Activation activation;
    public Candidate candidate;

    private static InterpretationNode[] EMPTY_INTERPRETATION_RELATIONS = new InterpretationNode[0];
    public InterpretationNode[] parents = EMPTY_INTERPRETATION_RELATIONS;
    public InterpretationNode[] children = EMPTY_INTERPRETATION_RELATIONS;

    public int isConflict = -1;
    public Conflicts conflicts = new Conflicts();

    public NavigableMap<Key, NodeActivation> nodeActivations;


    public boolean isPrimitive() {
        return orInterpretationNodes == null || orInterpretationNodes.isEmpty() || (orInterpretationNodes.size() == 1 && orInterpretationNodes.contains(doc.bottom));
    }


    public enum Relation {
        EQUALS,
        CONTAINS,
        CONTAINED_IN;

        public boolean compare(InterpretationNode a, InterpretationNode b) {
            switch (this) {
                case EQUALS:
                    return a == b;
                case CONTAINS:
                    return a.contains(b, false);
                case CONTAINED_IN:
                    return b.contains(a, false);
                default:
                    return false;
            }
        }
    }


    public InterpretationNode(Document doc, int primId, int id, int length, Decision s) {
        this(doc, primId, id);
        this.length = length;
        this.state = s;
    }


    public InterpretationNode(Document doc, int primId, int id, int length) {
        this(doc, primId, id);
        this.length = length;
    }


    public InterpretationNode(Document doc, int primId, int id) {
        this.doc = doc;
        this.primId = primId;
        this.id = id;
    }


    public void setState(Decision newState, long v) {
        if ((fixed && state != Decision.UNKNOWN && newState != Decision.UNKNOWN) ||
                newState == Decision.UNKNOWN && v != visitedState) return;

        state = newState;
        visitedState = v;

        changeSelectedRecursive(doc.visitedCounter++);
    }


    public static boolean checkSelfReferencing(InterpretationNode nx, InterpretationNode ny, boolean onlySelected, int depth) {
        if (ny.isBottom()) return false;
        if (nx == ny || nx.contains(ny, true)) return true;

        if (depth > MAX_SELF_REFERENCING_DEPTH) return false;

        Set<InterpretationNode> orIN = onlySelected ? ny.selectedOrInterpretationNodes : ny.orInterpretationNodes;
        if (orIN != null) {
            for (InterpretationNode n : orIN) {
                if (checkSelfReferencing(nx, n, onlySelected, depth + 1)) return true;
            }
        }

        return false;
    }


    private void changeSelectedRecursive(long v) {
        if(visitedChangeSelected == v) return;
        visitedChangeSelected = v;

        boolean newSelected = isSelected(v);

        if (isSelected != newSelected) {
            if(refByOrInterpretationNode != null) {
                for (InterpretationNode ref : refByOrInterpretationNode) {
                    ref.setSelectedOrInterpretationNode(this, newSelected);
                }
            }

            isSelected = newSelected;

            if(!isBottom()) {
                for (InterpretationNode cn : children) {
                    cn.changeSelectedRecursive(v);
                }
            }
        }
    }


    private void setSelectedOrInterpretationNode(InterpretationNode n, boolean state) {
        if (state) {
            if (selectedOrInterpretationNodes == null) {
                selectedOrInterpretationNodes = new TreeSet<>();
            }
            selectedOrInterpretationNodes.add(n);
        } else {
            if(selectedOrInterpretationNodes != null) {
                selectedOrInterpretationNodes.remove(n);
            }
        }
    }


    public boolean isSelected(long v) {
        if(primId >= 0) return state == Decision.SELECTED;

        if(visitedGetState == v) return true;
        visitedGetState = v;

        for(InterpretationNode pn: parents) {
            if(!pn.isSelected(v)) return false;
        }
        return true;
    }


    private void computeLargestCommonSubsetIncremental(InterpretationNode no) {
        if (orInterpretationNodes.size() == 0) {
            setLCS(no);
            return;
        }
        long vMin = doc.visitedCounter++;
        List<InterpretationNode> results = new ArrayList<>();
        if(largestCommonSubset != null) {
            largestCommonSubset.computeLargestCommonSubsetRecursiveStep(results, doc.visitedCounter++, vMin, 2, 0);
        }
        no.computeLargestCommonSubsetRecursiveStep(results, doc.visitedCounter++, vMin, 2, 0);
        setLCS(InterpretationNode.add(doc, true, results));
    }


    private void setLCS(InterpretationNode lcs) {
        if (largestCommonSubset != null) {
            largestCommonSubset.linkedByLCS.remove(this);
        }
        largestCommonSubset = lcs;
        if (largestCommonSubset != null) {
            if(largestCommonSubset.linkedByLCS == null) {
                largestCommonSubset.linkedByLCS = new TreeSet<>();
            }
            largestCommonSubset.linkedByLCS.add(this);
        }
    }


    private void computeLargestCommonSubsetRecursiveStep(List<InterpretationNode> results, long v, long vMin, int s, int depth) {
        if (visitedComputeLargestCommonSubset == v) return;
        if (visitedComputeLargestCommonSubset <= vMin) largestCommonSubsetCount = 0;
        visitedComputeLargestCommonSubset = v;
        largestCommonSubsetCount++;

        if(depth > 10) return;

        if (largestCommonSubsetCount == s) {
            results.add(this);
            return;
        }

        for (InterpretationNode pn : parents) {
            pn.computeLargestCommonSubsetRecursiveStep(results, v, vMin, s, depth + 1);
        }

        if (largestCommonSubset != null) {
            largestCommonSubset.computeLargestCommonSubsetRecursiveStep(results, v, vMin, s, depth + 1);
        }
    }


    public void addOrInterpretationNode(InterpretationNode n) {
        if (orInterpretationNodes == null) {
            orInterpretationNodes = new TreeSet<>();
        }
        computeLargestCommonSubsetIncremental(n);
        orInterpretationNodes.add(n);
        if (n.refByOrInterpretationNode == null) {
            n.refByOrInterpretationNode = new TreeSet<>();
        }
        n.refByOrInterpretationNode.add(this);

        setSelectedOrInterpretationNode(n, n.isSelected(doc.visitedCounter++));
    }


    public Collection<NodeActivation> getNodeActivations() {
        return nodeActivations != null ? nodeActivations.values() : Collections.emptySet();
    }


    public static InterpretationNode add(Document doc, boolean nonConflicting, InterpretationNode... input) {
        ArrayList<InterpretationNode> in = new ArrayList<>();
        for (InterpretationNode n : input) {
            if (n != null && !n.isBottom()) in.add(n);
        }
        return add(doc, nonConflicting, in);
    }


    public static InterpretationNode add(Document doc, boolean nonConflicting, List<InterpretationNode> inputs) {
        if (inputs.size() == 0) return doc.bottom;

        for (Iterator<InterpretationNode> it = inputs.iterator(); it.hasNext(); ) {
            if (it.next().isBottom()) {
                it.remove();
            }
        }

        if (inputs.size() == 1 || (inputs.size() == 2 && inputs.get(0) == inputs.get(1))) {
            InterpretationNode n = inputs.get(0);
            if (nonConflicting && n.isConflicting(doc.visitedCounter++)) return null;
            return n;
        }

        ArrayList<InterpretationNode> parents = new ArrayList<>();
        ArrayList<InterpretationNode> children = new ArrayList<>();
        computeRelations(doc, parents, children, inputs);

        if (parents.size() == 1) {
            InterpretationNode n = parents.get(0);
            if (nonConflicting && n.isConflicting(doc.visitedCounter++)) return null;
            return n;
        }

        if (nonConflicting) {
            for (InterpretationNode p : parents) {
                if (p.isConflicting(doc.visitedCounter++)) {
                    return null;
                }
            }
        }

        InterpretationNode n = new InterpretationNode(doc, -1, doc.interpretationIdCounter++);

        n.linkRelations(parents, children, doc.visitedCounter++);

        n.length = n.computeLength(doc.visitedCounter++);

        n.minPrim = Integer.MAX_VALUE;
        n.maxPrim = Integer.MIN_VALUE;
        for(InterpretationNode in: inputs) {
            n.minPrim = Math.min(n.minPrim, in.minPrim);
            n.maxPrim = Math.max(n.maxPrim, in.maxPrim);
        }

        return n;
    }


    private static Comparator<InterpretationNode> LENGTH_COMP = new Comparator<InterpretationNode>() {
        @Override
        public int compare(InterpretationNode n1, InterpretationNode n2) {
            return Integer.compare(n2.length, n1.length);
        }
    };


    private static void computeRelations(Document doc, List<InterpretationNode> parentsResults, List<InterpretationNode> childrenResults, List<InterpretationNode> inputs) {
        if (inputs.isEmpty()) return;
        long v = doc.visitedCounter++;
        int i = 0;
        int s = inputs.size();

        Collections.sort(inputs, LENGTH_COMP);

        if (s == 2 && inputs.get(1).primId >= 0 && inputs.get(1).children.length == 0) {
            parentsResults.addAll(inputs);
            return;
        }

        for(int pass = 0; pass <= 1; pass++) {
            for (InterpretationNode n : inputs) {
                n.computeParents(parentsResults, v, pass);
            }
            v = doc.visitedCounter++;
        }

        if(parentsResults.size() == 1) return;
        assert parentsResults.size() != 0;

        for (InterpretationNode n : inputs) {
            n.computeChildren(childrenResults, doc.visitedCounter++, v, inputs.size(), 0);
        }

        inputs.get(0).computeChildren(childrenResults, doc.visitedCounter++, v, inputs.size(), 1);
    }


    private void computeParents(List<InterpretationNode> parentResults, long v, int pass) {
        if (visitedComputeParents == v || length == 0) return;
        visitedComputeParents = v;

        for (InterpretationNode pn: parents) {
            pn.computeParents(parentResults, v, pass);
        }

        boolean flag = true;
        for(InterpretationNode cn: children) {
            if(pass == 0) {
                if (cn.visitedNumberInnerInputs != v) {
                    cn.numberInnerInputs = 0;
                    cn.visitedNumberInnerInputs = v;
                }
                cn.numberInnerInputs++;
            }

            if(cn.numberInnerInputs == cn.parents.length) {
                cn.computeParents(parentResults, v, pass);
                flag = false;
            }
        }

        if(flag && pass == 1) {
            parentResults.add(this);
        }
    }


    private void computeChildren(List<InterpretationNode> childrenResults, long v, long nv, int s, int pass) {
        if (visitedComputeChildren == v) return;

        if (pass == 0) {
            if (visitedComputeChildren <= nv) {
                numberOfInputsComputeChildren = 0;
            }
            numberOfInputsComputeChildren++;
        }

        visitedComputeChildren = v;

        if(pass == 1 && numberOfInputsComputeChildren == s) {
            boolean covered = false;
            for(InterpretationNode pn: parents) {
                if(pn.numberOfInputsComputeChildren == s) {
                    covered = true;
                    break;
                }
            }

            if(!covered) {
                childrenResults.add(this);
            }
        } else {
            for (InterpretationNode cn : children) {
                cn.computeChildren(childrenResults, v, nv, s, pass);
            }
        }
    }


    private int computeLength(long v) {
        if (visitedComputeLength == v) return 0;
        visitedComputeLength = v;

        if (primId >= 0) return 1;

        int result = 0;
        for (InterpretationNode p : parents) {
            result += p.computeLength(v);
        }
        return result;
    }


    private void linkRelations(List<InterpretationNode> pSet, List<InterpretationNode> cSet, long v) {
        for (InterpretationNode p : pSet) {
            addLink(p, this);
        }
        for (InterpretationNode c : cSet) {
            c.visitedLinkRelations = v;
            addLink(this, c);
        }

        for (InterpretationNode p : pSet) {
            ArrayList<InterpretationNode> tmp = new ArrayList<>();
            for (InterpretationNode c : p.children) {
                if (c.visitedLinkRelations == v) {
                    tmp.add(c);
                }
            }

            for (InterpretationNode c : tmp) {
                removeLink(p, c);
            }
        }
    }


    private static void addLink(InterpretationNode a, InterpretationNode b) {
        a.children = Utils.addToArray(a.children, b);
        b.parents = Utils.addToArray(b.parents, a);
    }


    private static void removeLink(InterpretationNode a, InterpretationNode b) {
        a.children = Utils.removeToArray(a.children, b);
        b.parents = Utils.removeToArray(b.parents, a);
    }


    public static InterpretationNode addPrimitive(Document doc) {
        assert doc != null;

        InterpretationNode n = new InterpretationNode(doc, doc.bottom.children.length, doc.interpretationIdCounter++, 1);

        n.minPrim = n.primId;
        n.maxPrim = n.primId;

        addLink(doc.bottom, n);
        return n;
    }


    public boolean isBottom() {
        return length == 0;
    }


    public boolean contains(boolean dir, InterpretationNode n, boolean followLCS) {
        boolean r;
        if (!dir) {
            r = contains(n, followLCS);
        } else {
            r = n.contains(this, followLCS);
        }
        return r;
    }


    public boolean contains(InterpretationNode n, boolean followLCS) {
        return contains(n, followLCS, doc.visitedCounter++);
    }


    private boolean contains(InterpretationNode n, boolean followLCS, long v) {
        visitedContains = v;

        if(!followLCS) {
            if(primId >= 0 && n.primId >= 0) {
                return primId == n.primId;
            }
            if(length <= n.length) {
                return false;
            }
        }

        if(this == n || n.isBottom()) {
            return true;
        }

        for(InterpretationNode p: parents) {
            if(n.maxPrim >= p.minPrim && n.minPrim <= p.maxPrim &&
                    (p.visitedContains != v && p.contains(n, followLCS, v))) {
                return true;
            }
        }

        if(followLCS && largestCommonSubset != null) {
            if(largestCommonSubset.contains(n, followLCS, v)) return true;
        }

        return false;
    }


    public void collectPrimitiveNodes(Collection<InterpretationNode> results, long v) {
        if(v == visitedCollect) return;
        visitedCollect = v;

        if(primId >= 0) {
            results.add(this);
        } else {
            for(InterpretationNode n: parents) {
                n.collectPrimitiveNodes(results, v);
            }
        }
    }


    public boolean isConflicting(long v) {
        if (isConflict >= 0) {
            return true;
        } else if(conflictsAllowed()) {
            if(visitedIsConflicting == v) return false;
            visitedIsConflicting = v;

            for(InterpretationNode p : parents) {
                if(p.isConflicting(v)) {
                    return true;
                }
            }
        }
        return false;
    }


    private boolean conflictsAllowed() {
        return nodeActivations == null || nodeActivations.isEmpty();
    }


    public String toString() {
        if(this == MIN) return "MIN_INTERPRETATION";
        if(this == MAX) return "MAX_INTERPRETATION";

        return toString(false);
    }


    private String toString(boolean level) {
        SortedSet<InterpretationNode> ids = new TreeSet<>();
        collectPrimitiveNodes(ids, doc.visitedCounter++);

        StringBuilder sb = new StringBuilder();
        sb.append("(");
        boolean f1 = true;
        for(InterpretationNode n: ids) {
            if(!f1) sb.append(",");
            f1 = false;
            sb.append(n.primId);
            sb.append(n.finalState.s);
            if(!level && n.orInterpretationNodes != null) {
                sb.append("[");
                boolean f2 = true;
                for(InterpretationNode on: n.orInterpretationNodes) {
                    if(!f2) sb.append(",");
                    f2 = false;
                    sb.append(on.toString(true));
                }
                sb.append("]");
            }
        }

        sb.append(")");
        return sb.toString();
    }


    @Override
    public int compareTo(InterpretationNode n) {
        int r = Integer.compare(length, n.length);
        if(r != 0) return r;
        return Integer.compare(id, n.id);
    }


    public static int compare(InterpretationNode oa, InterpretationNode ob) {
        if(oa == ob) return 0;
        if(oa == null && ob != null) return -1;
        if(oa != null && ob == null) return 1;
        return oa.compareTo(ob);
    }
}
