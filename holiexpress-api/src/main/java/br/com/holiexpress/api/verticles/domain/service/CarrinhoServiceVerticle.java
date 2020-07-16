package br.com.holiexpress.api.verticles.domain.service;

import java.util.List;
import java.util.stream.Collectors;

import br.com.holiexpress.api.util.CarrinhoUtil;
import br.com.holiexpress.api.verticles.domain.model.dto.ProdutoDTO;
import br.com.holiexpress.api.verticles.domain.model.enumeradores.EventBusEnum;
import br.com.holiexpress.api.verticles.domain.model.enumeradores.ProdutoEnum;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class CarrinhoServiceVerticle extends AbstractVerticle {
	
	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		adicionarAoCarrinho();
		excluirDoCarrinho();
		super.start(startPromise);
	}
	
	Promise<Void> adicionarAoCarrinho() {
		vertx.eventBus().consumer(EventBusEnum.CARRINHO_ADD_SERVICE.getValor(), 
								  handlerController ->{
			JsonArray fromController = (JsonArray)handlerController.body();
			List<ProdutoDTO> produtosRequest = CarrinhoUtil.getInstance().listaProdutoRequest(fromController);
			List<ProdutoDTO> produtosSession = CarrinhoUtil.getInstance().listaProdutoSession(fromController);
			JsonArray produtosToGet = CarrinhoUtil.getInstance().jsonArrayListProdutoSomenteId(produtosRequest);
			JsonArray toSession = CarrinhoUtil.getInstance().jsonArrayListProdutoFormatado(produtosSession);
			vertx.eventBus().request(EventBusEnum.PRODUTO_LIST_IDS_REPOSITORY.getValor(), 
									 produtosToGet, 
									 handlerRepository -> {
				JsonArray fromRepository = (JsonArray)handlerRepository.result().body();
				if(!produtosSession.isEmpty()) {					
					carrinhoAtualAdicionar(toSession, fromRepository, produtosRequest);
				}else {
					carrinhoVazioAdicionar(fromRepository, produtosRequest, toSession);
				}
				handlerController.reply(toSession);
			});
		});
		return Promise.promise();
	}
	
	Promise<Void> excluirDoCarrinho() {
		vertx.eventBus().consumer(EventBusEnum.CARRINHO_EXCLUIR_SERVICE.getValor(), 
								  handlerController ->{
			JsonArray fromController = (JsonArray)handlerController.body();
			List<ProdutoDTO> produtosRequest = CarrinhoUtil.getInstance().listaProdutoRequest(fromController);
			List<ProdutoDTO> produtosSession = CarrinhoUtil.getInstance().listaProdutoSession(fromController);
			List<ProdutoDTO> novoCarrinho = CarrinhoUtil.getInstance().listaProdutoSession(fromController);
			produtosRequest.forEach(solicitadoExclusao -> {
				ProdutoDTO paraExcluir = (ProdutoDTO)solicitadoExclusao;
				if(produtosSession.contains(paraExcluir)) {
					novoCarrinho.remove(paraExcluir);
				}
			});
			handlerController.reply(CarrinhoUtil.getInstance().jsonArrayListProdutoFormatado(novoCarrinho));
		});
		return Promise.promise();
	}
	
	private void carrinhoVazioAdicionar(JsonArray fromRepository, List<ProdutoDTO> produtosRequest, JsonArray toSession) {
		fromRepository.stream().forEach(object -> {
			JsonObject itemRepository = (JsonObject)object;
			produtosRequest.stream().forEach(ObjectRequest -> {
				ProdutoDTO itemRequest = (ProdutoDTO)ObjectRequest;
				if(itemRepository.getInteger(ProdutoEnum.ID.getValor()).equals(itemRequest.getId().intValue())) {
					itemRepository.put(ProdutoEnum.QUANTIDADE.getValor(), itemRequest.getQuantidade());
					carrinhoVerificarQuantidadeCalcularPreco(itemRepository, new JsonObject(Json.encode(itemRequest)));
				}
			});
			itemRepository.remove(ProdutoEnum.QUANTIDADE_ESTOQUE.getValor());
			toSession.add(itemRepository);
		});
	}
	
	private void carrinhoAtualAdicionar(JsonArray toSession, JsonArray fromRepository, List<ProdutoDTO> produtosRequest) {
		List<ProdutoDTO> produtosSessionTemp = CarrinhoUtil.getInstance().listaProdutoRepositoty(toSession);
		List<ProdutoDTO> novosProdutosAddCarrinho = CarrinhoUtil.getInstance()
											.listaProdutoRepositoty(fromRepository)
											.stream()
											.filter(produto -> !produtosSessionTemp.contains(produto))
											.collect(Collectors.toList());
		novosProdutosAddCarrinho.stream().forEach(produto ->{
			JsonObject toFormat = new JsonObject(Json.encode(produto));
			toFormat.remove(ProdutoEnum.QUANTIDADE_ESTOQUE.getValor());
			toFormat.remove(ProdutoEnum.FROM_REQUEST.getValor());
    		toSession.add(toFormat);
    	});
		produtosRequest.stream().forEach(produtoRequest -> {
			fromRepository.stream().forEach(objectFromRepository ->{
				JsonObject itemRepository = (JsonObject)objectFromRepository;
				if(itemRepository.getInteger(ProdutoEnum.ID.getValor()).equals(produtoRequest.getId().intValue())) {
					itemRepository.put(ProdutoEnum.QUANTIDADE.getValor(), produtoRequest.getQuantidade());
				}
			});
		});
		fromRepository.forEach(object -> {
			JsonObject itemRepository = (JsonObject)object;
			toSession.forEach(objectInSession -> {
				JsonObject itemSession = (JsonObject)objectInSession;
				carrinhoVerificarQuantidadeCalcularPreco(itemRepository, itemSession);
			});
		});
	}
	
	private void carrinhoVerificarQuantidadeCalcularPreco(JsonObject itemRepository, JsonObject itemSession) {
		if(itemRepository.getInteger(ProdutoEnum.ID.getValor()).equals(itemSession.getInteger(ProdutoEnum.ID.getValor()))) {
			Integer quantidade = itemRepository.getInteger(ProdutoEnum.QUANTIDADE.getValor());
			Double preco = itemRepository.getDouble(ProdutoEnum.PRECO.getValor());
			if(quantidade > itemRepository.getInteger(ProdutoEnum.QUANTIDADE_ESTOQUE.getValor())) {
				itemSession.put(ProdutoEnum.MENSAGEM_CAMPO.getValor(), ProdutoEnum.MENSAGEM.getValor());
				itemSession.put(ProdutoEnum.QUANTIDADE.getValor(), itemRepository.getInteger(ProdutoEnum.QUANTIDADE_ESTOQUE.getValor()));
				itemSession.remove(ProdutoEnum.PRECO.getValor());
				itemSession.put(ProdutoEnum.PRECO.getValor(), itemRepository.getInteger(ProdutoEnum.QUANTIDADE_ESTOQUE.getValor()) * preco);
			}
			if(quantidade <= itemRepository.getInteger(ProdutoEnum.QUANTIDADE_ESTOQUE.getValor())) {
				itemSession.remove(ProdutoEnum.MENSAGEM_CAMPO.getValor());
				itemSession.put(ProdutoEnum.QUANTIDADE.getValor(), quantidade);
				itemSession.remove(ProdutoEnum.PRECO.getValor());
				itemSession.put(ProdutoEnum.PRECO.getValor(), quantidade * preco);
			}
		}
	}
	
}
