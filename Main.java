/*
Ronan Quill 17040434
Art Maguire 16150201
Rastislav Salplachta 17244382
Patrick Elligott 17239427
*/

package com.company;

import java.util.*;
import javax.swing.*;


import java.util.Comparator;

//0 1 2 3 4 5 6 7 8

public class Main {
    class BlankTileLocation{
        public int column;
        public int row;

        public BlankTileLocation(){

        }

        public BlankTileLocation(BlankTileLocation another) {
            this.column = another.column;
            this.row = another.row;
        }

        void FindBlankTileLocation(ArrayList<ArrayList<String>>  state){
            for(int i = 0; i < state.size(); i++){
                for(int j = 0; j < state.get(i).size(); j++){
                    if(state.get(i).get(j).equals("0")){
                        row = i;
                        column = j;
                        return;
                    }
                }
            }
        }
    }

    class Node{
        public ArrayList<ArrayList<String>>  state;
        public Node parent;

        public int g = 0;
        public int h = 0;
        public int f = 0;

        public BlankTileLocation blankTileLocation;

        public Node(){
            blankTileLocation = new BlankTileLocation();
        }

        public Node(Node another) {
            this.g = another.g;
            this.h = another.h;
            this.f = another.f;

            this.blankTileLocation = new BlankTileLocation(another.blankTileLocation);

            this.state = new ArrayList<ArrayList<String>>(another.state);
            for(int i = 0; i < state.size(); i++){
                state.set(i, new ArrayList<String>(another.state.get(i)));
            }

            this.parent = another.parent;
        }

        @Override
        public int hashCode() {
            String hash = new String();
            hash = HashMatrix(state);
            return hash.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;

            Node rhs = (Node) obj;
            return CompareMatrices(state, rhs.state);
        }
    }

    class LowestFSCoreComparator implements Comparator<Node>
    {
        @Override
        public int compare(Node x, Node y)
        {
           return Integer.compare(x.f, y.f);
        }
    }

    public JFrame f;

    public ArrayList<String> startState = new ArrayList<String>();
    public ArrayList<String> endState = new ArrayList<String>();
    public int matrixDimensions = 0;

    //-----------------------------------------------------------------------------------------------------------
    //Input Functions--------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------------
    boolean GetStartState(){
        String startStateRaw = JOptionPane.showInputDialog(f,"Enter Start State");
        StringTokenizer tokenizer = new StringTokenizer(startStateRaw, " ");

        if(tokenizer.countTokens() != 9){
            System.out.println("Invalid Start State: 9 unique values are required in the range 0 -> 8. Your input was " + startStateRaw);
            return false;
        }
        startState.clear();

        while(tokenizer.hasMoreTokens()){
            String nextToken = tokenizer.nextToken();
            if(startState.contains(nextToken)){
                System.out.println("Invalid Start State: 9 unique values are required in the range 0 -> 8. Your input was " + startStateRaw);
                return false;
            }else{
                startState.add(nextToken);
            }
        }

        //Table must be a square matrix
        matrixDimensions = (int)Math.sqrt(startState.size());

        return true;
    }

    boolean GetEndState(){
        String endStateRaw = JOptionPane.showInputDialog(f,"Enter End State");
        StringTokenizer tokenizer = new StringTokenizer(endStateRaw, " ");

        if(tokenizer.countTokens() != 9){
            System.out.println("Invalid End State: 9 unique values are required in the range 0 -> 8. Your input was " + endStateRaw);
            return false;
        }
        endState.clear();

        while(tokenizer.hasMoreTokens()){
            String nextToken = tokenizer.nextToken();
            if(endState.contains(nextToken)){
                System.out.println("Invalid End State: 9 unique values are required in the range 0 -> 8. Your input was " + endStateRaw);
                return false;
            }else{
                endState.add(nextToken);
            }
        }

        return true;
    }

    void ConvertListToMatrix(ArrayList<String> list, ArrayList<ArrayList<String>> matrix){
        //Deduce the correct size of the matrix.
        //Lists are always converted into square matrices
        int listIndex = 0;
        int matrixSize = (int)Math.sqrt(list.size());

        for(int i = 0; i < matrixSize; i++){
            matrix.add(new ArrayList<String>());
            for(int j = 0; j < matrixSize; j++){
                matrix.get(i).add(list.get(listIndex));

                listIndex++;
            }
        }
    }

    //-----------------------------------------------------------------------------------------------------------
    //A* Functions-----------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------------
    boolean MoveLeft(ArrayList<ArrayList<String>> state, BlankTileLocation blankTileLocation){
        if(blankTileLocation.column > 0 && blankTileLocation.column < state.get(blankTileLocation.row).size()) {
            String currentElement = state.get(blankTileLocation.row).get(blankTileLocation.column);
            String rightElement = state.get(blankTileLocation.row).get(blankTileLocation.column-1);

            state.get(blankTileLocation.row).set(blankTileLocation.column, rightElement);
            state.get(blankTileLocation.row).set(blankTileLocation.column-1, currentElement);

            blankTileLocation.column -= 1;
            return true;
        }else{
            return false;
        }
    }

    boolean MoveRight(ArrayList<ArrayList<String>> state, BlankTileLocation blankTileLocation){
        if(blankTileLocation.column >= 0 && blankTileLocation.column < state.get(blankTileLocation.row).size()-1) {
            String currentElement = state.get(blankTileLocation.row).get(blankTileLocation.column);
            String rightElement = state.get(blankTileLocation.row).get(blankTileLocation.column+1);

            state.get(blankTileLocation.row).set(blankTileLocation.column, rightElement);
            state.get(blankTileLocation.row).set(blankTileLocation.column+1, currentElement);

            blankTileLocation.column += 1;
            return true;
        }else{
            return false;
        }
    }

    boolean MoveUp(ArrayList<ArrayList<String>> state, BlankTileLocation blankTileLocation) {

        if (blankTileLocation.row > 0 && blankTileLocation.row < state.size()) {
            String currentElement = state.get(blankTileLocation.row).get(blankTileLocation.column);
            String upElement = state.get(blankTileLocation.row - 1).get(blankTileLocation.column);

            state.get(blankTileLocation.row).set(blankTileLocation.column, upElement);
            state.get(blankTileLocation.row - 1).set(blankTileLocation.column, currentElement);

            blankTileLocation.row -= 1;

            return true;
        } else {
            return false;
        }
    }

    boolean MoveDown(ArrayList<ArrayList<String>> state, BlankTileLocation blankTileLocation){

        if(blankTileLocation.row >= 0 && blankTileLocation.row < state.size()-1) {
            String currentElement = state.get(blankTileLocation.row).get(blankTileLocation.column);
            String downElement = state.get(blankTileLocation.row+1).get(blankTileLocation.column);

            state.get(blankTileLocation.row).set(blankTileLocation.column, downElement);
            state.get(blankTileLocation.row+1).set(blankTileLocation.column, currentElement);

            blankTileLocation.row += 1;

            return true;
        }else{
            return false;
        }
    }

    int ElementsOutOfPlace(ArrayList<ArrayList<String>> state){
        int elementsOutOfPlace = 0;

        int endStateIndex = 0;
        for(int row = 0; row < state.size(); row++){
            for(int column = 0; column < state.get(row).size(); column++){
                String currentStateElement = state.get(row).get(column);
                String endStateElement = endState.get(endStateIndex);

                if(!currentStateElement.equals(endStateElement)){
                    elementsOutOfPlace++;
                }
                endStateIndex++;
            }
        }

        return elementsOutOfPlace;
    }

    boolean CompareMatrices(ArrayList<ArrayList<String>> first, ArrayList<ArrayList<String>> second){

        for(int row = 0; row < first.size(); row++)
        {
            for(int column = 0; column < first.get(row).size(); column++) {
                if(!first.get(row).get(column).equals(second.get(row).get(column))){
                    return false;
                }
            }
        }

        return true;
    }

    void AStar(ArrayList<ArrayList<String>> start, ArrayList<ArrayList<String>> end){
        //Set<Node> openList = new HashSet<Node>();
        Set<Node> closedList = new HashSet<Node>();
        Map<Node, Integer> gScores = new HashMap<Node, Integer>();

        Comparator<Node> comparator = new LowestFSCoreComparator();
        PriorityQueue<Node> openList = new PriorityQueue<Node>(comparator);

        Node startNode = new Node();
        startNode.state = start;
        startNode.blankTileLocation.FindBlankTileLocation(start);
        startNode.g = 0;
        startNode.h = ElementsOutOfPlace(start);
        startNode.f = startNode.h + startNode.g;


        openList.add(startNode);
        gScores.put(startNode, startNode.g);

        while(!openList.isEmpty()){
            //Get lowest f score
            Node currentNode = openList.poll();
            closedList.add(currentNode);
            PrintMatrix(currentNode.state);
            System.out.println("F: " + currentNode.f);

            //if end state reconstruct path
            if(CompareMatrices(currentNode.state, end))
            {
                //We found a solution;
                PrintMatrix(currentNode.state);
                return;
            }

            //Get moves
            ArrayList<Node> successorList = new ArrayList<Node>();

            Node MoveLeft = new Node(currentNode);
            if(MoveLeft(MoveLeft.state, MoveLeft.blankTileLocation))
            {
                System.out.println("Left");
               // PrintMatrix(MoveLeft.state);
                successorList.add(MoveLeft);
            }

            Node MoveRight = new Node(currentNode);
            if(MoveRight(MoveRight.state, MoveRight.blankTileLocation))
            {
                System.out.println("Right");
               // PrintMatrix(MoveRight.state);
                successorList.add(MoveRight);
            }

            Node MoveUp = new Node(currentNode);
            if(MoveUp(MoveUp.state, MoveUp.blankTileLocation))
            {
                System.out.println("Up");
                //PrintMatrix(MoveUp.state);
                successorList.add(MoveUp);
            }

            Node MoveDown = new Node(currentNode);
            if(MoveDown(MoveDown.state, MoveDown.blankTileLocation))
            {
                System.out.println("Down");
                //PrintMatrix(MoveDown.state);
                successorList.add(MoveDown);
            }

            for(int currentSuccessorIndex =  0; currentSuccessorIndex < successorList.size(); currentSuccessorIndex++){
                Node currentSuccessor = successorList.get(currentSuccessorIndex);

                if(closedList.contains(currentSuccessor)){
                    //ignore this node
                }else{
                    currentSuccessor.parent = currentNode;
                    currentSuccessor.g = currentNode.g + 1;
                    currentSuccessor.h = ElementsOutOfPlace(currentSuccessor.state);
                    currentSuccessor.f = currentSuccessor.g + currentSuccessor.h;

                    if(!openList.contains(currentSuccessor)){
                        openList.add(currentSuccessor);
                        gScores.put(currentSuccessor, currentSuccessor.g);
                        System.out.println("Child F: " + currentSuccessor.f);
                    }
                    else{
                        int g = gScores.get(currentSuccessor);
                        if(currentSuccessor.f < g) {

                            openList.remove(currentSuccessor);
                            openList.add(currentSuccessor);
                            gScores.put(currentSuccessor, currentSuccessor.g);

                            System.out.println("Better G: " + currentSuccessor.g + " Old G: " + g);
                        }
                    }
                }
            }
            //Loop through move available moves
                //IF in closedList, then ignore it

                //Set G, H, F Score
                //Set its parent
                //if not in openList then add to it

        }
    }

    String HashMatrix(ArrayList<ArrayList<String>> matrix){
        String hash = "";

        for(int row = 0; row < matrix.size(); row++) {
            for (int column = 0; column < matrix.get(row).size(); column++) {
                hash+=matrix.get(row).get(column);
            }
        }

        return hash;
    }

    void PrintMatrix(ArrayList<ArrayList<String>> matrix){
        for(int row = 0; row < matrix.size(); row++) {
            for (int column = 0; column < matrix.get(row).size(); column++) {
                String currentElement = matrix.get(row).get(column);
                System.out.print(currentElement + " ");
            }
            System.out.println();
        }
    }
    //-----------------------------------------------------------------------------------------------------------
    //Main Function----------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------------
    public static void main(String[] args) {
	// write your code here
        Main aStar = new Main();
        aStar.f=new JFrame();

        while(!aStar.GetStartState()){}

        while(!aStar.GetEndState()){}

        ArrayList<ArrayList<String>> startMatrix = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> endMatrix = new ArrayList<ArrayList<String>>();

        aStar.ConvertListToMatrix(aStar.startState, startMatrix);
        aStar.ConvertListToMatrix(aStar.endState, endMatrix);
        aStar.PrintMatrix(startMatrix);

        System.out.println("A*");

        aStar.AStar(startMatrix, endMatrix);

        //System.out.println(aStar.HashMatrix(matrix));

        System.out.println("Goodbye World");
    }
}
