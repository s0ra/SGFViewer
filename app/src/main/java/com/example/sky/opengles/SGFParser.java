package com.example.sky.opengles;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SGFParser {

    private int[][] goban = new int[19][19];
    private BufferedReader bufferedReader;
    private ArrayList<GameNode> gameNodes = new ArrayList<GameNode>();
    private int lastNodeID = 0;
    private int depth = -1;

    public SGFParser(String pathname) {
        File file =  new File(Environment.getExternalStorageDirectory(), pathname);
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 19; j++) {
                goban[i][j] = 0;
            }
        }
    }

    public void CloseParser() throws IOException {
        bufferedReader.close();
    }

    private void parseID(int id) throws IOException {

        // get new line

        String line = bufferedReader.readLine();
        String[] node = line.split("((?<=[\\;\\(\\)\\[\\]])|(?=[\\;\\(\\)\\[\\]]))");

        boolean isNextPoint = false;
        String nextColor = "B";
        boolean handicap = false;

        Log.d(line, line);

        for(String p: node) {
            if (p.equals("(")) {
                depth += 1;
            } else if (p.equals(")")) {
                depth -= 1;
            }
            if (p.equals("AB")) {
                handicap = true;
                Log.d("AB", "AB");
                gameNodes.add(0, new GameNode(goban));
                for (int i = 0; i < 19; i++) {
                    for (int j = 0; j < 19; j++) {
//                        Log.d("node", Integer.toString(gameNodes.get(0).getGoban()[i][j]));
                    }
                }
                isNextPoint = true;
                nextColor = "B";
            } else if (p.equals("B")) {
                Log.d("B", "B");
                isNextPoint = true;
                if (gameNodes.size() > 0 && !handicap) {
                    gameNodes.add(new GameNode(gameNodes.get(lastNodeID)));
                    gameNodes.get(lastNodeID).setChild(gameNodes.get(lastNodeID + 1), depth);
                } else if (!handicap) {
                    gameNodes.add(new GameNode(goban));
                }
                gameNodes.get(lastNodeID).setChild(gameNodes.get(lastNodeID + 1), depth);
                nextColor = "B";

                Log.d("next", "B");
            } else if (p.equals("W")) {
                handicap = false;
                Log.d("W", "W");
                isNextPoint = true;
                if (gameNodes.size() > 0) {
                    gameNodes.add(new GameNode(gameNodes.get(lastNodeID)));
                    gameNodes.get(lastNodeID).setChild(gameNodes.get(lastNodeID+1), depth);
                } else {
                    gameNodes.add(new GameNode(goban));
                }
                nextColor = "W";

                Log.d("next", "W");
            } else if (isNextPoint){
                Log.d("next", p);
                if (p.matches("[a-z]+")) {
                    gameNodes.get(lastNodeID).setGoban(addStone(nextColor, p));

                    for (int i = 0; i < 19; i++) {
                        for (int j = 0; j < 19; j++) {
//                            Log.d("node", Integer.toString(gameNodes.get(lastNodeID).getGoban()[i][j]));
                        }
                    }

                    if (!handicap) {
                        lastNodeID += 1;
                    }
                }
            } else if (p.matches("[A-Z]+")) {
                isNextPoint = false;
            }
        }
    }

    public int[][] getGoban(int id) {
        if (gameNodes != null) {

            Log.d("size", Integer.toString(gameNodes.size()));
            if (gameNodes.size() <= id) {
                Log.d("id", Integer.toString(id));
                for (int s = gameNodes.size(); s <= id; s++) {
                    try {
                        parseID(s);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (gameNodes.size() > 0) {
            for (int i = 0; i < 19; i++) {
                for (int j = 0; j < 19; j++) {
//                    Log.d("node", Integer.toString(gameNodes.get(id-1).getGoban()[i][j]));
                }
            }
            return gameNodes.get(id).getGoban();
        }
        return new int[19][19];
    }

    private int[][] addStone(String color, String point) {
        int[][] newGoban = new int[19][19];
        goban = gameNodes.get(lastNodeID).getGoban();
        newGoban = goban;
        int coor[] = pointToNums(point);
        if (color.equals("B")) {
            newGoban[coor[0]][coor[1]] = 1;
        } else if (color.equals("W")) {
            newGoban[coor[0]][coor[1]] = 2;
        }
        goban = newGoban;
        gameNodes.set(lastNodeID, gameNodes.get(lastNodeID).setGoban(newGoban));

        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 19; j++) {
//                Log.d("hi", Integer.toString(newGoban[i][j]));
            }
        }

        return newGoban;
    }

    private int[] pointToNums(String point) {
        int numbers[] = new int[2];
        numbers[0] = point.charAt(0) - 'a';
        numbers[1] = point.charAt(1) - 'a';
        return numbers;
    }
}
