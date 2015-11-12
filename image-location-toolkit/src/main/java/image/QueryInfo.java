package image;

/**
 * Created by 571743 on 11/5/2015.
 */
public interface QueryInfo {
    //builds the information set about a query
    QueryInfo buildQueryInfo(String json);

    String[] getKeys();

    String[] getValues();

    boolean getAllAnyChoice();

    boolean getThesChoice();

    String getJsonString();

    String toString();
}
