package it.polimi.softeng.is25am10.model.cards;

import it.polimi.softeng.is25am10.model.boards.Coordinate;
import it.polimi.softeng.is25am10.model.boards.GoodsBoard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Output {
    Map<String, Integer> cash = new HashMap<>();
    Map<String, List<Coordinate>> killedCrew = new HashMap<>();
    Map<String, Coordinate> removed = new HashMap<>();
    Map<String, List<GoodsBoard.Type>> rewards = new HashMap<>();
}