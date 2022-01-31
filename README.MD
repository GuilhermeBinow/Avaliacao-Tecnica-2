# Bem-vindo à Avaliacao Tecnica

Bem-vindo, essa avaliacao visa a criacao de uma aplicacao de API para controle de um sistema de pautas. 
O sistema foi criado em Java SpringBoot, e possui o seu funcionamento da seguinte maneira:

Para Acessar as funcoes de API, será necessario utilizar a Postman collection na pasta do projeto.
as instrucoes de uso das variaveis para a collection estao inseridas diretamente na pagina de uso

#Requisitos de funcionamento
A aplicação necessita que Postgresql esteja instalado e rodando na porta 5432 para ser executada, tanto quanto a criacao das tables pauta e pautateste para funcionar.
Se a aplicação não funcionar via docker, é possivel roda-la como spring application localmente com todas as funcionalidades.
# Funcionamento da Aplicação

A aplicacao funciona como um controlador de votos para um sistema de pautas, cujo os dados não são perdidos ao reiniciar a aplicação.
o objeto Pauta possui todas as informações necessárias para o controle dessas informações, que consiste de:
<br />*ID da Pauta*, não é controlavel pelo sistema, gerado automaticamente
<br />*Numero de Associados*, numero de votos que podem ser contabilizados para a pauta
<br />*Nome da Pauta* o nome desejado para a Pauta
<br />*Status Pauta* um controle de estado, constando de:
    *Fechada(pautas são criadas fechadas): Pautas onde não houve votação ainda;
    *Aberta: Pautas abertas para votação;
    *Concluida: Pautas que já foram votadas.
<br />*Voto Sim e Voto Nao* Contador de votos para quando a Pauta já foi aberta<br />

# Votação de Pautas
Para votar numa pauta, ela necessita estar aberta, para isso, é necessario usar o request Abre Pauta da collection fornecida,
ao abrir, será solicitado o ID da pauta e o tempo de abertura, que é quantos minutos é desejado que ela permaneça aberta. Caso não seja fornecido um tempo de abertura, será usado o padrão de 1 minuto

#Contagem de votos
Para checar o número de votos de uma pauta, deve esperar que a mesma esteja fechada e executar o request Contabilizar Votos da Pauta,
que irá retornar uma lista de String com o número de votos sim e não, que também pode ser visto através dos requests Verificar Pautas ou Verificar Pauta Especifica.

#Tabelas e SQL
Para manter a persistencia de dados das pautas, foi implementado uma integração ao PostgreSQL, que mantem os dados mesmo quando a aplicação é reiniciada,
para isso, foram criadas duas Tables, Pautas e Associados.
<br />A Table Pautas guarda as informações das pautas, exemplificadas acima, enquanto a Table Associados guarda os dados de quem votou em qual Pauta, e qual seu ID,
caso seja desejado o seu uso para implementação de alguma função futura, mas também para impedir que um mesmo ID seja rejistrado com 2 votos em uma Pauta.

#Tratamento de erros
Todos os erros são abordados da mesma forma pelo programa, com um IllegalStateException, seguido de uma mensagem com o que falhou

#Retorno e Logs
A aplicaçao responde em forma de Log. Todos os metodos invocados e a sua funcao. Não é apresentado ao usuario

# Versão beta 0.1
Essa versão foi chamada de 0.1 beta, pois as maiores funcoes ja foram implementadas. Conforme o uso da aplicacao e melhorias forem aplicadas e bugs resolvidos, é possivel remover do beta e fazer uma public release de 1.0

