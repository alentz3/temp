package image;

/**
 * This class is used to parse data and insert it into fields that are easy to use for database operations.
 */
public class QueryInfoImpl implements QueryInfo {

    String JsonString;
    boolean allAnyChoice;
    boolean ThesChoice;
    String[] keys;
    String[] values;
    /*
     * Constructor - initializes JsonString with value of parameter, and initializes keys and values as empty strings.
     *
     * @param json String representation of JSON object to use for database query
     */
    public QueryInfoImpl(){

    }

    //builds the information set about a query
    @Override
    public QueryInfo buildQueryInfo(String json) {
        JsonString = json;


        String[] everything = json.split("&");
        int i = 0;
        if(everything[0].startsWith("allAny")){
            if (everything[0].equals("allAny=on")){
                allAnyChoice = true;
            }
            else{
                allAnyChoice = false;
            }
            i++;
        }
        if(everything[0].startsWith("thesaurus")){
            if (everything[0].equals("thesaurus=on")){
                ThesChoice = true;
            }
            else{
                ThesChoice = false;
            }
            i++;
        }
        if(everything[1].startsWith("thesaurus")){
            if (everything[1].equals("thesaurus=on")){
                ThesChoice = true;
            }
            else{
                ThesChoice = false;
            }
            i++;
        }
        keys = new String[(everything.length- i)/2];
        values = new String[(everything.length- i)/2];
        /*
        Everything on the front end has a key in the json parsed array followed by a value. This next code block puts a
        key to a value then skips ahead 2 because it will go to the next key
         */
        for(int x= 0; i < everything.length ; i+=2, x++ ){
            String[] first_half = everything[i].split("=");
            String[] second_half = everything[i+1].split("=");
            if(first_half.length >1 && second_half.length > 1) {
                keys[x] = first_half[1];
                values[x] = second_half[1];
            }
        }
        return this;
    }

    /**
     * Returns the keys as an array of strings.
     *
     * @return keys as an array of strings
     */
    @Override
    public String[] getKeys(){
        return keys;
    }

    /**
     * Returns the values as an array of strings.
     *
     * @return values as an array of strings
     */
    @Override
    public String[] getValues(){
        return values;
    }

    /**
     * Returns the status of allAnyChoice (true or false).
     *
     * @return the Boolean value of allAnyChoice
     */
    @Override
    public boolean getAllAnyChoice(){
        return allAnyChoice;
    }

    /**
     * Returns the status of getThesChoice (true or false).
     *
     * @return the Boolean value of getThesChoice
     */
    @Override
    public boolean getThesChoice(){
        return ThesChoice;
    }

    /**
     * Returns the JSON string used for the query.
     *
     * @return JSON string used for the query
     */
    @Override
    public String getJsonString(){
        return JsonString;
    }

    /**
     *  Returns a String representing the QueryInfo object.
     *  Overrides java.lang.Object.toString().
     *
     * @return a String representing the QueryInfo object
     */
    @Override
    public String toString(){
        String str ="keys: ";
        for(String s: keys){
            if(s != null)
                str += s + ", ";
        }
        str+= "\n values: ";
        for(String s: values){
            if(s != null)
                str+= s + ",";
        }
        if(allAnyChoice){
            str+= "AllAny = true";
        }else{
            str+= "AllAny = false";
        }
        str += "\n";
        if(ThesChoice){
            str+= "Thes = true";
        }else{
            str+= "Thes = false";
        }
        return str;
    }
}
