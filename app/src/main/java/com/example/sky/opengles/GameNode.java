package com.example.sky.opengles;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class GameNode {
    private String name = "";
    private String comment = "";
    private int[][] goban;
    private int id;
    private ArrayList<Integer> variationID = new ArrayList<Integer>();
    private boolean isRoot = false;
    private GameNode variationRoot;
    private GameNode parent;
    private ArrayList<GameNode> childs = new ArrayList<GameNode>();

    public int[][] getGoban() {
        return goban;
    }

    public GameNode setGoban(int[][] goban) {
        this.goban = goban;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    GameNode (int[][] root) {
        isRoot = true;
        id = 0;
        this.goban = root;
    }

    GameNode (GameNode parent) {
        goban = new int[19][19];
        goban = parent.getGoban();
        id = parent.getID() + 1;

    }

    public int getID() {
        return id;
    }

    public GameNode setChild(GameNode child, int variationID) {
        child.setParent(this);
        this.childs.add(child);
        return this.childs.get(variationID);
    }

    public void setParent(GameNode parent) {
        this.parent = parent;
    }

    public GameNode getParent() {
        return parent;
    }

    public GameNode getVariationRoot() {
        return variationRoot;
    }

    public GameNode getChild(int variationID) {
        return(childs.get(variationID));
    }

    public void setVariationRoot(GameNode root) {
        variationRoot = root;
    }

    public GameNode getChild() {
        return(childs.get(0));
    }
}
