## Docker:
	- Certifique-se que o Docker está rodando.
	- Abra o prompt de comando com privilégios de administrador, ou sudo.
	- Navegue até o caminhno do Dockerfile, no meu caso copiei os arquivos para minha vm Linux: /home/nsio/projetos/ilegra/workspace/holiexpress-api, no windows era: C:\Java\ilegra\workspace\holiexpress-api
	- Execute o comando: sudo docker-compose up --build
	- Após fazer o build, nas próximas execuções, pode executar o comando: docker-compose up
	- Para o caso dessa solução em específico, após levantar o postgres no Docker, será necessário, executar o comando: sudo ifconfig, identificar o ip que o serviço está rodando, e editar o arquivo PgEnum.java, que está na aplicação com os dados do ip. No meu caso o ip 127.25.0.1, o arquivo PgEnum, fico assim:
			HOST("172.25.0.1"),
			URL("jdbc:postgresql://172.25.0.1:5432/holiexpress"),
			DATA_BASE("holiexpress"),
			USER("postgres"),
			PASSWORD("Postgres2020!"); 
	- Abra outro prompt.
	- Execute o comando: sudo docker build -t holiexpress-api.jar .
	- Em seguida execute o comando: sudo docker run -t -i -p 8080:8080 holiexpress-api.jar
	OBS
		- Meu dockerfile, que está indo junto ao projeto está configurado para rodar no Linux, se você for rodar no windows acho que será necessário mudar o valor da propriedade, ENV VERTICLE_HOME /usr/verticles, mesmo no linux será necessário informar o caminho onde estão os arquivos do projeto.

## BANCO DE DADOS
- Foi usado o postgresql, rodei o banco de dados em uma máquina Linux(Ubuntu 20) virtual, rodando na VMWare, pois o Docker Desktop não rodu de jeito nenhum na verão do windows que tenho instalado na minha máquina.
Passo a passo sobre como configurar o postgres no Docker, e em seguida mostrar que arquivo de configuração na aplicação deve ser alterado receber os dados de acesso da instancia do banco de dado que você configurar. Pré requisitos, ter uma versão do Docker instalada na sua máquina ou em uma máquina virtual.
- FlyWay
	- O sistema está fazendo o controle dos spcripts do banco de dados utilizando o FlyWay, mas o mesmo não dá suporte para criação do banco de dados. Nesse caso, antes de rodar a aplicação, acesse o o banco de dados que acabamos de "levantar" no Docker, pode utilizar o pgAdmin4, e use os dados dos comandos acima para acessar a instancia do banco que está rodando, então, execute o seguinte script:
		CREATE DATABASE holiexpress
		    WITH 
		    OWNER = postgres
		    ENCODING = 'UTF8'
		    LC_COLLATE = 'Portuguese_Brazil.1252'
		    LC_CTYPE = 'Portuguese_Brazil.1252'
		    TABLESPACE = pg_default
		    CONNECTION LIMIT = -1;
	Confirme a criação do banco de dados, verificando o banco de dados através da interface do pgAdmi4. Pronto com o banco de dados criado, pode prossguir para próxima etapa e rodar o aplicativo Vert.x

- Terminal/Prompt
	- Navegue até a raiz do projeto, no meu caso: C:\Java\ilegra\workspace\holiexpress-api
	- Execute o comando: mvn package (É preciso tem o mamven configurado no ambiente do OS)
	- Em seguida execute o comando: java -jar target/holiexpress-api-1.0.0-SNAPSHOT.jar

- Eclipse
	- Acesse Run Configurations
	- Na opção maven build, clique duas vezes
	- Selecione o projeto
	- No campo goals, digite o comando: clean clean compile vertx:run
	- Clique em Run

## AUTENTICAÇÃO
- Primeiro tentei usar o OAuth2, criei a aplicação com secret key e client id, no cloud do google, mas o escopo para autenticar diretamente não foi aprovado, diente disso a aplicação não consegue pegar diretamente o param code, sem passar pela tela de consemento, com esse empeditivo, decidir utilizar o sistema básico de autenticação do Vert.x, utilizando o JWT. Na calsse WebVeticle, no final dela está toda a implementação da autenticação utilizando o OAuth2, porém comentado, pois não está sendo utilizando.
Existe ainda uma implementação para autenticação que utiliza certificados com pares de chaves para geração do token, o exemplo também está comentado no código.
	PASSO A PASSO - ENTRAR NO SISTEMA
	- Primeiro faça uma requisão, POST, para esse recurso: http://localhost:8080/holiexpress-api/users, passando no body, o seguinte playload: {
		"name":"Nysio",
		"e-mail":"[seu e-mail]",
		"login":"[seu login]",
		"password":"[seu password]"
	}
	- Com isso, um usuário acaba de ser cadastrado no sistema, agora é necessário fazer o login no sistema, e então um token de autenticação será gerado. Para logar no sistema, faça uma requisição POST, para esse recuro: http://localhost:8080/holiexpress-api/users/login, passando no body, o seguinte playload: {
		"login":"[seu login]",
		"password":"[sua senha]"
	}
	- Em seguida, o sistema enviará essa resposta: {
	  "id" : [id cadastrado],
	  "name" : "[nome cadastrado]",
	  "e-mail" : "[email cadastrado]",
	  "login" : "[login cadastrado]",
	  "password" : "[senha cadastrada]",
	  "token" : "[token gerado]"
	}
	- Agora, é necessário copiar o valor enviado no campo token, que veio na resposta do passo anterior, e para cada recurso restrito do sistema que for acessar, será necessário configrar o HEADER, colocando a seguinte informação:
	KEY = Authorization , VALUE = Bearer [token gerado no passo anterior]

## OBSERVAÇÔES
	- No caso da utilização do postman, o que ocorre é que esse software guarda um cookie com as informações de autorização, então, se for enviado uma requisição para um recurso protegiso, e as informações do token não forem passadas, e mesmo assim conseguir o acesso a esse recuro, nesse caso, é necessário desabilitar a criação de cookies pelo postman, assim verá que requisições a recursos privados, sem a informação não serão autorizados.

## RECURSOS
	- Públicos:
		POST: http://localhost:8080/holiexpress-api/usuarios/login
		POST: http://localhost:8080/holiexpress-api/usuarios
	- Restritos
		GET: http://localhost:8080/holiexpress-api/restrito/usuarios
		GET: http://localhost:8080/holiexpress-api/restrito/usuarios/{id}
		POST: http://localhost:8080/holiexpress-api/restrito/produtos/
		GET: http://localhost:8080/holiexpress-api/restrito/produtos/
		GET: http://localhost:8081/holiexpress-api/restrito/produtos/{id}
		POST: http://localhost:8080/holiexpress-api/restrito/carrinho
		GET: http://localhost:8080/holiexpress-api/restrito/carrinho
		DELETE: http://localhost:8082/holiexpress-api/restrito/carrinho

	- EXEMPLOS DE PLAYLOAD PARA CADA RECURSO

		- POST: http://localhost:8080/holiexpress-api/usuarios/login
			{
				"login":"[seu login]",
				"password":"[sua senha]"
			}
			- RETORNO
				- Se houver sucesso retorna o usuário cadastrado e adiciona o campo com o token gerado na resposta. Caso contrário, retorna a mensagem do erro.

		- POST: http://localhost:8080/holiexpress-api/usuarios
			{
				"nome":"[nome]",
				"email":"[email]",
				"login":"[login]",
				"password":"[senha]"
			}
			- RETORNO
				- O usuário cadastrado com identificador, ou a mensagem do erro ocorrido.

		- GET: http://localhost:8080/holiexpress-api/restrito/usuarios
			- Esse recurso não possui playload para ser enviado no body. 
			- Obrigatório informar o token no HEADER da requisição.
			- RETORNO
				- Retorna lista de usuários cadastrados, ou a mensagem do erro ocorrido.

		- GET: http://localhost:8080/holiexpress-api/restrito/usuarios/{id}
			- O identificador do usuário de ser informado como parametro na url, no campo: {id}
			- Esse recurso não possui playload para ser enviado no body. 
			- Obrigatório informar o token no HEADER da requisição.
			- RETORNO
				- Retorna um usuário para o {id} informado, ou a mensagem do erro ocorrido.
			

		- POST: http://localhost:8080/holiexpress-api/restrito/produtos/
			- Obrigatório informar o token no HEADER da requisição.
			- Deve ser enviado uma lista de produtos, ou apenas um produto, mas deve seguir a notação de lista, usando o [], e informar os pordutos, mesmo que seja apenas um, segue um exemplo:
			[
				{
					"descricao":"Visitar a Escandinávia",
					"qtd_estoque":10,
					"preco":10000.00,
					"id_user":41
				},
				{
					"descricao":"Enviar Flores",
					"qtd_estoque":10,
					"preco":10000.00,
					"id_user":41
				}
			]
			- RETORNO
				- Retorna a lista com os produtos que acabaram de ser criados, ou uma mensagem com o erro ocorrido.

		- GET: http://localhost:8080/holiexpress-api/restrito/produtos/
			- Esse recurso não possui playload para ser enviado no body.
			- Obrigatório informar o token no HEADER da requisição.
			- RETORNO
				- Retorna lista de produtos cadastrados, ou a mensagem do erro ocorrido.

		- GET: http://localhost:8081/holiexpress-api/restrito/produtos/{id}
			- O identificador do produto de ser informado como parametro na url, no campo: {id}
			- Esse recurso não possui playload para ser enviado no body. 
			- Obrigatório informar o token no HEADER da requisição.

			- RETORNO
				- Retorna um produto para o {id} informado, ou a mensagem do erro ocorrido.

		- POST: http://localhost:8080/holiexpress-api/restrito/carrinho
			- Obrigatório informar o token no HEADER da requisição.
			- Deve ser enviado uma lista de produtos, ou apenas um produto, mas deve seguir a notação de lista, usando o [], e informar os pordutos, mesmo que seja apenas um. Para o carrinho, além de informar o código do produto deve ser informado a quantidade que deseja adicionar, segue um exemplo:
			[
				{
					"id":86,
					"quantidade":5
				},
				{
					"id":88,
					"quantidade":1
				},
				{
					"id":84,
					"quantidade":9
				}
			]
			- RETORNO
				- Retorna a lista com os produtos que foram adicionados ao carrinho, com a quantidade e valor do preço atualizados ou uma mensagem com o erro ocorrido.
		
		- GET: http://localhost:8082/holiexpress-api/restrito/carrinho
			- Esse recurso não possui playload para ser enviado no body. 
			- Obrigatório informar o token no HEADER da requisição.
			-RETORNO
				- Retorna lista de produtos formatada com os pordutos que estão atualmente no carrinho. A informação dos produtos no carrinho dura enquanto a Session estiver ativa, o default são 30 minutos ou a mensagem do erro ocorrido.

		- DELETE: http://localhost:8082/holiexpress-api/restrito/carrinho
			- Obrigatório informar o token no HEADER da requisição.
			- Deve ser enviado uma lista de produtos, ou apenas um produto, mas deve seguir a notação de lista, usando o [], e informar os pordutos, mesmo que seja apenas um, segue um exemplo:
			[
				{
					"id":86,
				},
				{
					"id":88
				}
			]
			- RETORNO
				- Retorna a lista com os produtos no carrinho, formatada e atualizada após a exclusão, ou uma mensagem com o erro ocorrido.	
