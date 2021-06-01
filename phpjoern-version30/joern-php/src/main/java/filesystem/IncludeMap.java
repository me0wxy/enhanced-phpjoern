package filesystem;

import graphutils.IncidenceListGraph;

import java.util.ArrayList;
import java.util.HashSet;

public class IncludeMap extends IncidenceListGraph<FSNode, FSEdge>{

    private FSNode root = null;

    public FSNode getRoot(){return root;}

    public void setRoot(){
        for(FSNode fsNode: getVertices()){
            if(inDegree(fsNode) == 0){
                root = fsNode;
                break;
            }
        }
    }

    public int numberOfFiles(){
        return getVertices().stream().filter(v->v.isFile()).toArray().length;
    }


    public FSNode getParentDirectory(FSNode child){
        ArrayList<FSNode> parentNode = new ArrayList<>();
        for(FSEdge edge: incomingEdges(child)){
            if(edge.getLabel().equals(FSEdge.TYPE_DIRECTORY_OF))
                parentNode.add(edge.getSource());
        }
        if(parentNode.size() == 1)
            return parentNode.get(0);
        // directory root, return itself
        else if(parentNode.size() == 0){
            return root;
        }
        else{
            System.err.println(child + "more than a parent directory");
            return null;
        }
    }

    public FSNode getChildDirectoryOrFile(FSNode parent, String name){
        for(FSEdge edge: outgoingEdges(parent)){
            if(edge.getLabel().equals(FSEdge.TYPE_DIRECTORY_OF)){
                FSNode fsNode = edge.getDestination();
                if(fsNode.getName().equals(name)){
                    return fsNode;
                }
            }
        }
        System.err.println("Directory/File " + name + " not found!");
        return null;
    }

    public HashSet<FSNode> getInclude(FSNode file){
        HashSet<FSNode> res = new HashSet<>();
        if(!getVertices().contains(file)) return res;
        for(FSEdge edge: outgoingEdges(file)){
            if(edge.getLabel().equals(FSEdge.TYPE_INCLUDE)){
                res.add(edge.getDestination());
            }
        }
        return res;
    }

    public HashSet<FSNode> getIncludeReverse(FSNode file){
        HashSet<FSNode> res = new HashSet<>();
        for(FSEdge edge: incomingEdges(file)){
            if(edge.getLabel().equals(FSEdge.TYPE_INCLUDE)){
                res.add(edge.getDestination());
            }
        }
        return res;
    }
}
