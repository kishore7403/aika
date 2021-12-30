package network.aika.steps;

import network.aika.neuron.activation.*;
import network.aika.direction.Direction;
import network.aika.linker.AbstractLinker;
import network.aika.neuron.bindingsignal.BindingSignal;

import java.util.List;

import static network.aika.direction.Direction.INPUT;
import static network.aika.direction.Direction.OUTPUT;

public abstract class LinkerStep<E extends Element, T extends AbstractLinker> extends TaskStep<E, T> {

    protected final BindingSignal bindingSignal;

    public LinkerStep(E element, BindingSignal bindingSignal, T task) {
        super(element, task);
        this.bindingSignal = bindingSignal;
    }

    protected List<Direction> getDirections() {
        if(getElement() instanceof InhibitoryActivation)
            return List.of(OUTPUT);

        return List.of(INPUT, OUTPUT);
    }
}
