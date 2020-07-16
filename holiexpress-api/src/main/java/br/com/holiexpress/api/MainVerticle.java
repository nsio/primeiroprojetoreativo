package br.com.holiexpress.api;

import br.com.holiexpress.api.verticles.domain.model.enumeradores.VerticlesEnum;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

public class MainVerticle extends AbstractVerticle {
	
	final JsonObject loadedConfig = new JsonObject();
	
	@Override
    public void start(Promise<Void> startFuture) throws Exception {
		loadConfig();
		configDeployments();
		super.start(startFuture);
    }
    
    Promise<JsonObject> loadConfig(){	
    	ConfigStoreOptions defaultConfig = new ConfigStoreOptions()
    			.setType(VerticlesEnum.CONFIG_TYPE.getValor())
    			.setFormat(VerticlesEnum.CONFIG_FORMAT.getValor())
    			.setConfig(new JsonObject().put("path", "config.json"));
    	ConfigStoreOptions cliConfig = new ConfigStoreOptions()
                 .setType(VerticlesEnum.CONFIG_FORMAT.getValor())
                 .setConfig(config());
    	ConfigRetrieverOptions opts = new ConfigRetrieverOptions()
                .addStore(defaultConfig)
                .addStore(cliConfig);
        ConfigRetriever cfgRetriever = ConfigRetriever.create(vertx, opts);
        loadedConfig.mergeIn(cfgRetriever.getCachedConfig());
        return Promise.promise();
    }
    
    Promise<Void> configDeployments(){
    	
    	DeploymentOptions opt = new DeploymentOptions()
				.setWorker(Boolean.TRUE)
				.setInstances(8)
				.setConfig(loadedConfig);
    	//CONFIG
    	vertx.deployVerticle(VerticlesEnum.DEPLOY_PATH_WEB_VERTICLE.getValor(), opt);
    	// USUARIO
    	vertx.deployVerticle(VerticlesEnum.DEPLOY_PATH_USUARIO_SERVICE.getValor(), opt);
    	vertx.deployVerticle(VerticlesEnum.DEPLOY_PATH_USUARIO_REPOSITORY.getValor(), opt);
    	// PRODUTO
    	vertx.deployVerticle(VerticlesEnum.DEPLOY_PATH_PRODUTO_SERVICE.getValor(), opt);
    	vertx.deployVerticle(VerticlesEnum.DEPLOY_PATH_PRODUTO_REPOSITORY.getValor(), opt);
    	// CARRINHO
    	vertx.deployVerticle(VerticlesEnum.DEPLOY_PATH_CARRINHO_SERVICE.getValor(), opt);
    	// DATA BASE
    	vertx.deployVerticle(VerticlesEnum.DEPLOY_PATH_DATA_BASE.getValor(), opt);
    	
    	return Promise.promise();
    }
    
}
