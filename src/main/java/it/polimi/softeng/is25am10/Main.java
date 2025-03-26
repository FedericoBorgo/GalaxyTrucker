package it.polimi.softeng.is25am10;


import it.polimi.softeng.is25am10.model.Model;

import java.util.function.BiConsumer;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException {
        Model model = new Model(2, new BiConsumer<Model, Model.State.Type>() {
            @Override
            public void accept(Model model, Model.State.Type type) {
                
            }
        });
    }
}