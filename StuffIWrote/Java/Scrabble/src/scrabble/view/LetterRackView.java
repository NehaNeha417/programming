package scrabble.view;

import javafx.scene.layout.HBox;
import scrabble.model.LetterModel;
import scrabble.model.LetterRack;

import java.util.Iterator;

/**
 * 9/30/16 7:51 PM
 */
class LetterRackView extends HBox {

    LetterRackView(LetterRack letterRack) {
        setSpacing(3);
        Iterator<LetterModel> it = letterRack.iterator();
        for (int i = 0; i < 7; i++) {
            LetterModel nextLetter = it.hasNext() ? it.next() : null;
            RackTileView tileView = new RackTileView(90, 90, nextLetter);
            getChildren().add(tileView);
        }
    }

    private void setTemporarilyActive(boolean b) {

    }
}
