package filesystem;

import inputModules.csv.PHPCSVNodeTypes;

public class FSNode {
    private String type;
    private String name;
    private Long id;
    public FSNode(){};
    public FSNode(String type, String name, Long id) {
        this.type = type;
        this.name = name;
        this.id = id;
    }
    public FSNode(FSNode n){
        this.type = n.type;
        this.name = n.name;
        this.id = n.id;
    }

    public void setType(String type) { this.type = type; }

    public String getType() { return type; }

    public void setName(String name) { this.name = name; }

    public String getName() { return name; }

    public void setId(Long id) { this.id = id; }

    public long getId() { return id; }

    public boolean isFile(){ return type.equals(PHPCSVNodeTypes.TYPE_FILE);}

    @Override
    public String toString(){
        return "[("+getId()+") "+getName()+"]";
    }
    @Override
    public boolean equals(Object o){
        if(o instanceof FSNode){
            FSNode n = (FSNode) o;
            return type.equals(n.type) && name.equals(n.name) && id.equals(n.id);
        }
        return false;
    }
    @Override
    public int hashCode(){
        return (type+name).hashCode()+id.intValue();
    }

}
