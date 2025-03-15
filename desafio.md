## Desafio

Gerenciador de tarefa

A Tarefa tem data de execucação

tarefa
- titulo
- descricao
- data limite da execução
- status = em andamento, pendente, concluido
- createdAt
sistema deve permitir que faça cadastro, listar(por data e título), editar e deletar tarefas

regra nenhuma tarefa deva ser registrado com data limite no passado

validação na entrada de dados
- titulo não pode ser vazio, ou menor que 20 caracteres

o sistema precisa gerar notificação, 
deve consultar no banco as tarefas que vencem no dia
e gerar um timer(talvez assincrono e enviar email)

