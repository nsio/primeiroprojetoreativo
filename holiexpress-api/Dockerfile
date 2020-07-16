FROM vertx/vertx3

ENV VERTICLE_NAME br.com.holiexpress.api.MainVerticle
ENV VERTICLE_FILE target/holiexpress-api.jar

# Set the location of the verticles
ENV VERTICLE_HOME /home/nsio/projetos/java/ilegra/desafio/workspace/holiexpress-api

EXPOSE 8080

# Copy your verticle to the container
COPY $VERTICLE_FILE $VERTICLE_HOME/

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec vertx run $VERTICLE_NAME -cp $VERTICLE_HOME/*"]