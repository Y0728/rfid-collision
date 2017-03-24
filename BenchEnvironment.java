import java.util.*;

/* The environment which creates the tags and readers and tests them */
public class BenchEnvironment{
    int numberOfTags = 15;
    int numberOfBitsInId = 4;
    int sizeOfId;
    Random randomGenerator = new Random();

    TreeSet<Integer> generatedUniqueIds;
    BSATag[] tags;

    public static void main(String[] args){
        new BenchEnvironment();
    }

    public BenchEnvironment(){
        generatedUniqueIds = new TreeSet<Integer>();
        tags = new BSATag[numberOfTags];

        //Fixing id size from number of bits
        sizeOfId = ((int)Math.pow(2, numberOfBitsInId))-1;

        generateID();
        generateBSATags();


        //DEBUG
        for(int i = 0; i < tags.length; i++){
            char[] s = tags[i].respondBSAQuery("0011".toCharArray());
            if(s != null){
                System.out.println(s);
            }
        }

    }

    /* Generate this test's unique ids
     * Random complexity (avoid with large set and many tags)*/
    public void generateID(){
        //TreeSet only takes unique values
        while(generatedUniqueIds.size() < numberOfTags){
            generatedUniqueIds.add(randomGenerator.nextInt(sizeOfId)+1);
        }
    }

    /* Create a BSAtag for all unique ids */
    public void generateBSATags(){
        int count = 0;
        for(int id : generatedUniqueIds){
            tags[count] = new BSATag(numberOfBitsInId, id);
            count++;
        }
    }
}
