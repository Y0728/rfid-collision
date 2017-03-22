/* The environment which creates the tags and readers and tests them */
public class BenchEnvironment{
    BSATag[] tags;

    public static void main(String[] args){
        new BenchEnvironment();
    }

    public BenchEnvironment(){
        tags = new BSATag[10];
        /*for(int i = 0; i < tags.length; i++){
            tags[i] = new BSATag()
        }*/
        BSATag tag = new BSATag(4, 10); // (1010)

    }
}
