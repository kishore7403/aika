package network.aika.elements.synapses;

import network.aika.elements.activations.Activation;
import network.aika.elements.activations.CategoryActivation;
import network.aika.elements.links.CategoryInputLink;
import network.aika.elements.neurons.CategoryNeuron;
import network.aika.elements.neurons.Neuron;

public abstract class CategoryInputSynapse extends DisjunctiveSynapse<
        CategoryInputSynapse,
        CategoryNeuron,
        Neuron<Activation>,
        CategoryInputLink,
        CategoryActivation,
        Activation
        > {

    public CategoryInputSynapse(Scope scope) {
        super(scope);
    }

    @Override
    public boolean isTrainingAllowed() {
        return false;
    }
}
