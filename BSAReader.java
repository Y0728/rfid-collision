
/* A reader */
public class BSAReader implements Reader{
    BSATag[] tags;
    int idLength;

    public BSAReader(BSATag[] tags, int numberOfBitsInId){
        this.tags = tags;
        idLength = numberOfBitsInId;
    }

    /* Uses algorithm to identify all tags */
    private void identifyTags(){

    }

    /* Sends the query to the cloud of tags */
    public void sendQuery(char[] query){
        char[] response;
        for(int i = 0; i < tags.length; i++){
            response = tags[i].respondBSAQuery(query);
        }
    }
}
