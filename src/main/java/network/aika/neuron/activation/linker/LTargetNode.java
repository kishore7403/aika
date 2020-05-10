package network.aika.neuron.activation.linker;

import network.aika.Document;
import network.aika.neuron.INeuron;
import network.aika.neuron.activation.Activation;

import java.lang.reflect.InvocationTargetException;

import static network.aika.neuron.activation.linker.Mode.INDUCTION;

public class LTargetNode<N extends INeuron> extends LNode<N> {

    public LTargetNode(Class<N> neuronClass, String label) {
        super(neuronClass, label);
    }

    public Activation follow(Mode m, INeuron n, Activation act, LLink from, Activation startAct) {
        if(n == null && m == INDUCTION) {
            try {
                n = neuronClass.getConstructor().newInstance();
            } catch (Exception e) {
            }
        }

        if(act == null) {
            Document doc = startAct.getDocument();
            act = new Activation(doc.getNewActivationId(), doc, n);
        }

        return act;
    }
}
