package image;

import image.BusinessLogic.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    @Bean
    public APIInteraction apiInteraction() {
        return new APIInteractionImpl();

    }
    @Bean
    public ImageDao databaseOperations() {
        return new DatabaseOperations();
    }

    @Bean
    public UploadAndExtractMD uEMD() {
        return new UploadAndExtractMDImpl();
    }

    @Bean
    public CryptoHandler cryptoHandler() {return new CryptoHandlerImpl();}

    @Bean
    public QueryInfo queryInfo() {return new QueryInfoImpl();}

    @Bean
    public DisplayLogic displayLogic() {return new DisplayLogicImpl();}

    @Bean
    public SearchLogic searchLogic() {return new SearchLogicImpl();}

    @Bean
    public EditAndDeleteLogic editAndDeleteLogic() {return new EditAndDeleteLogicImpl();}
}