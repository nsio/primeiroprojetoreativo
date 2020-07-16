package br.com.holiexpress.api.verticles;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

import br.com.holiexpress.api.verticles.domain.model.enumeradores.PgEnum;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class DataBaseVerticle extends AbstractVerticle {

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		doDatabaseMigrations();
		super.start(startPromise);
	}
	
	Promise<Void> doDatabaseMigrations() {
	    Flyway flyway = Flyway.configure().dataSource(PgEnum.URL.getValor(), 
	    											  PgEnum.USER.getValor(), 
	    											  PgEnum.PASSWORD.getValor()).load();
	    flyway.baseline();
	
	    try {
	    	flyway.migrate();
	    	return Promise.promise();
	    } catch (FlywayException fe) {
	    	fe.printStackTrace();
	    	return Promise.promise();
	    }
    }
	
}
