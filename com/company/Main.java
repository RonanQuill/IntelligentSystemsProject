/*
Ronan Quill 17040434
Art Maguire 16150201
Rastislav Salplachta 17244382
Patrick Elligott 17239427
*/

package com.company;

import java.util.*;
import javax.swing.*;

public class Main {

    public JFrame jFrame = new JFrame();

    //-----------------------------------------------------------------------------------------------------------
    //Helper Classes--------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------------
    //This is a helper class which conveniently stores the position of the 0/blank tile location in each node.
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

    //This node class represents a node that A* searches
    class Node{
        public ArrayList<ArrayList<String>>  state;
        public Node parent;

        public int g = 0;
        public int h = 0;
        public int f = 0;

        public BlankTileLocation blankTileLocation;

        //Default constructor
        public Node(){

            blankTileLocation = new BlankTileLocation();
        }

        //Copy Constructor
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

        //Overriden functions to allow node to be used with HashSet
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

    //This is custom comparator used to allow the node class to be used with PriorityQueue
    class LowestFSCoreComparator implements Comparator<Node>
    {
        @Override
        public int compare(Node x, Node y)
        {
            return Integer.compare(x.f, y.f);
        }
    }

    //A simple enum to help distinguish whether we are playing the 8 puzzle or the 15 puzzle
    enum GameType {
        EightPuzzle,
        FifteenPuzzle
    }
    //-----------------------------------------------------------------------------------------------------------
    //Input Functions--------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------------
    boolean GetState(ArrayList<String> state, String inputMessage, GameType gameType){
        String stateRaw = JOptionPane.showInputDialog(jFrame, inputMessage);
        StringTokenizer tokenizer = new StringTokenizer(stateRaw, " ");

        int maxRange = 8; //8 puzzle is the default option, the comparision is here anyway for completeness.
        if(gameType == GameType.EightPuzzle){
            maxRange = 8;
        }
        else if(gameType == GameType.FifteenPuzzle){
            maxRange = 15;
        }

        //Here we tokenize our input string and ensure that there are the correct amount of tokens and that they are all unique in the given range
        if(tokenizer.countTokens() == maxRange+1){
            state.clear();

            while(tokenizer.hasMoreTokens()){
                String nextToken = tokenizer.nextToken();
                if(state.contains(nextToken) || (Integer.parseInt(nextToken) > maxRange && Integer.parseInt(nextToken) >= 0)){
                    System.out.println("Invalid State: " + (maxRange+1) + " unique values are required in the range 0 -> " + maxRange + ". Your input was " + stateRaw);
                    return false;
                }else{
                    state.add(nextToken);
                }
            }
        }
        else{
            System.out.println("Invalid State: " + (maxRange+1) + " unique values are required in the range 0 -> " + maxRange + ". Your input was " + stateRaw);
            return false;
        }

        return true;
    }

    //A function which displays an option dialog which allows the user to select which game type we are playing
    GameType GetGameType(){
        List<String> optionList = new ArrayList<>();

        optionList.add("8 Puzzle");
        optionList.add("15 Puzzle");

        Object[] options = optionList.toArray();

        int value = JOptionPane.showOptionDialog(
                null,
                "Please select a Puzzle Size",
                "Pick",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                optionList.get(1));

        String opt = optionList.get(value);
        System.out.println("You picked " + opt);

        if(value == 1)
        {
            return GameType.FifteenPuzzle;
        }
        else
        {
            return GameType.EightPuzzle;
        }
    }

    //-----------------------------------------------------------------------------------------------------------
    //A* Functions-----------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------------
    //This will move the current blank tile left if it is a valid move and returns if it is indeed valid.
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

    //This will move the current blank tile right if it is a valid move and returns if it is indeed valid.
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
    //This will move the current blank tile up if it is a valid move and returns if it is indeed valid.
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

    //This will move the current blank tile down if it is a valid move and returns if it is indeed valid.
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

    //This is the heuristic used in our implementation, it simply counts how many tiles are out of place.
    int ElementsOutOfPlace(ArrayList<ArrayList<String>> state, ArrayList<ArrayList<String>> endState){
        int elementsOutOfPlace = 0;

        for(int row = 0; row < state.size(); row++){
            for(int column = 0; column < state.get(row).size(); column++){
                String currentStateElement = state.get(row).get(column);
                String endStateElement = endState.get(row).get(column);

                if(!currentStateElement.equals(endStateElement)){
                    elementsOutOfPlace++;
                }
            }
        }

        return elementsOutOfPlace;
    }

    //This is a function which will compare each element of the matrices and determine if they identical
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

    Stack<Node> ReconstructPath(Node endNode, Node startNode){
        Stack<Node> path = new Stack<>();

        Node currentNode = endNode;
        path.push(currentNode);
        while(currentNode != startNode) { //A simple address comparison is actually okay here
            currentNode = currentNode.parent;
            path.push(currentNode);
        }
        return path;
    }

    //The function which performs the A* search.
    Stack<Node> AStar(ArrayList<ArrayList<String>> start, ArrayList<ArrayList<String>> end){
        Set<Node> closedList = new HashSet<Node>();

        Comparator<Node> comparator = new LowestFSCoreComparator();
        PriorityQueue<Node> openList = new PriorityQueue<Node>(comparator);

        //Set up the starting node.
        Node startNode = new Node();
        startNode.state = start;
        startNode.blankTileLocation.FindBlankTileLocation(start);
        startNode.g = 0;
        startNode.h = ElementsOutOfPlace(start, end);
        startNode.f = startNode.h + startNode.g;

        openList.add(startNode);

        while(!openList.isEmpty()){
            Node currentNode = openList.poll();
            closedList.add(currentNode);

            if(CompareMatrices(currentNode.state, end))
            {
                //We found a solution;
                PrintMatrix(currentNode.state);
                return ReconstructPath(currentNode, startNode);
            }

            //Get successor moves
            ArrayList<Node> successorList = new ArrayList<Node>();

            Node MoveLeft = new Node(currentNode);
            if(MoveLeft(MoveLeft.state, MoveLeft.blankTileLocation))
            {
                successorList.add(MoveLeft);
            }

            Node MoveRight = new Node(currentNode);
            if(MoveRight(MoveRight.state, MoveRight.blankTileLocation))
            {
                successorList.add(MoveRight);
            }

            Node MoveUp = new Node(currentNode);
            if(MoveUp(MoveUp.state, MoveUp.blankTileLocation))
            {
                successorList.add(MoveUp);
            }

            Node MoveDown = new Node(currentNode);
            if(MoveDown(MoveDown.state, MoveDown.blankTileLocation))
            {
                successorList.add(MoveDown);
            }

            //Loop through move available moves
            for(int currentSuccessorIndex =  0; currentSuccessorIndex < successorList.size(); currentSuccessorIndex++) {
                Node currentSuccessor = successorList.get(currentSuccessorIndex);

                if (closedList.contains(currentSuccessor)) {
                    //ignore this node as it has already been evaluated.
                } else {
                    //Set G, H, F Score and set its parent
                    currentSuccessor.parent = currentNode;
                    currentSuccessor.g = currentNode.g + 1;
                    currentSuccessor.h = ElementsOutOfPlace(currentSuccessor.state, end);
                    currentSuccessor.f = currentSuccessor.g + currentSuccessor.h;

                    openList.add(currentSuccessor);
                }
            }
        }

        return new Stack<Node>(); //If no path just return an empty path.
    }

    //This is the function required for the interim submission which takes in the start state and prints the moves that are available
    //This function has a small bit of boilerplate code along with it as it is mimicking the final submission A* function that we created
    void GetPossibleMoves(ArrayList<ArrayList<String>> start){

        //Set up the starting node.
        Node startNode = new Node();
        startNode.state = start;
        startNode.blankTileLocation.FindBlankTileLocation(start);

        //Get successor moves
        Node MoveLeft = new Node(startNode);
        if(MoveLeft(MoveLeft.state, MoveLeft.blankTileLocation))
        {
            String valueMoved = MoveLeft.state.get(startNode.blankTileLocation.row).get(startNode.blankTileLocation.column);
            System.out.println(valueMoved + " to the east.");
        }

        Node MoveRight = new Node(startNode);
        if(MoveRight(MoveRight.state, MoveRight.blankTileLocation))
        {
            String valueMoved = MoveRight.state.get(startNode.blankTileLocation.row).get(startNode.blankTileLocation.column);
            System.out.println(valueMoved + " to the west.");
        }

        Node MoveUp = new Node(startNode);
        if(MoveUp(MoveUp.state, MoveUp.blankTileLocation))
        {
            String valueMoved = MoveUp.state.get(startNode.blankTileLocation.row).get(startNode.blankTileLocation.column);
            System.out.println(valueMoved + " to the south.");
        }

        Node MoveDown = new Node(startNode);
        if(MoveDown(MoveDown.state, MoveDown.blankTileLocation))
        {
            String valueMoved = MoveDown.state.get(startNode.blankTileLocation.row).get(startNode.blankTileLocation.column);
            System.out.println(valueMoved + " to the north.");
        }
    }

    //-----------------------------------------------------------------------------------------------------------
    //Helper Functions-----------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------------
    void ConvertListToMatrix(ArrayList<String> list, ArrayList<ArrayList<String>> matrix){
        //Lists are always converted into square matrices
        int listIndex = 0;
        int matrixSize = (int)Math.sqrt(list.size());

        for(int row = 0; row < matrixSize; row++){
            matrix.add(new ArrayList<String>());
            for(int column = 0; column < matrixSize; column++){
                matrix.get(row).add(list.get(listIndex));

                listIndex++;
            }
        }
    }

    //This hash function is extremely simple int that it just converts the matrix of numbers in a string which in turn will use the built in Java string hasher.
    String HashMatrix(ArrayList<ArrayList<String>> matrix){
        String hash = "";

        for(int row = 0; row < matrix.size(); row++) {
            for (int column = 0; column < matrix.get(row).size(); column++) {
                hash+=matrix.get(row).get(column);
            }
        }

        return hash;
    }

    void PrintMatrix(ArrayList<ArrayList<String>> matrix) {
        for (int row = 0; row < matrix.size(); row++) {
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
        Main aStar = new Main();

        ArrayList<String> startState = new ArrayList<String>();
        ArrayList<String> endState = new ArrayList<String>();

        GameType gameType = aStar.GetGameType();

        while(!aStar.GetState(startState, "Enter Start State", gameType)){}
        while(!aStar.GetState(endState, "Enter End State", gameType)){}

        ArrayList<ArrayList<String>> startMatrix = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> endMatrix = new ArrayList<ArrayList<String>>();

        aStar.ConvertListToMatrix(startState, startMatrix);
        aStar.ConvertListToMatrix(endState, endMatrix);

        System.out.println("A*");

       /* Stack<Node> path = aStar.AStar(startMatrix, endMatrix);

        int stepIndex = 1;
        while(!path.empty()){
            Node currentNode = path.pop();
            System.out.println("Step " + stepIndex);
            aStar.PrintMatrix(currentNode.state);
            stepIndex++;
        }*/

        aStar.GetPossibleMoves(startMatrix);

        System.out.println("Goodbye World");
        System.exit(0);
    }
}
