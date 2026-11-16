# Integração com o front-end ElevateControl v0.5

**Este repositório é o backend; o frontend não foi modificado automaticamente.**

O front possui `VITE_AUTH_MODE=api` e `VITE_API_URL=http://localhost:8080/api`, mas `src/context/AppContext.tsx` ainda usa `repository.ts` com `localStorage`. Apenas a autenticação passa a conversar com o Spring ao ativar o modo API; as páginas de domínio exigem integração adicional.

## 1. Ativar o login

No arquivo `.env` do front:

```
VITE_AUTH_MODE=api
VITE_API_URL=http://localhost:8080/api
```

## 2. Adicionar token CSRF ao `src/services/http.ts`

O backend protege as mudanças de dados contra CSRF. **Antes de qualquer POST/PATCH/DELETE/PUT**, buscar `GET /api/auth/csrf` com `credentials:'include'`. O servidor devolve um JSON como `{ "headerName":"X-XSRF-TOKEN", "token":"..." }` e um cookie CSRF.

Exemplo (adaptar ao `apiRequest` já existente no front):

```ts
async function csrfHeader(baseUrl: string): Promise<Record<string, string>> {
  const response = await fetch(`${baseUrl}/auth/csrf`, { credentials: 'include' });
  if (!response.ok) throw new Error('Falha ao obter token CSRF');
  const data: { headerName: string; token: string } = await response.json();
  return { [data.headerName]: data.token };
}

// Dentro do apiRequest, antes de chamar fetch:
const antiCsrf = method === 'GET' ? {} : await csrfHeader(BASE_URL);
// Nos headers: ...antiCsrf
```

**Nota:** reobter o token antes das mutações é simples e robusto durante o desenvolvimento; em produção pode-se cachear com renovação após login/logout.

## 3. Integrar as coleções e funções do AppContext

Trocar `getInitialData`/`persistData` pelo carregamento de `/usuarios` (apenas admin), `/clientes` (apenas admin), `/predios`, `/elevadores`, `/chamados`, `/checklists` (histórico acessível). Não solicite coleções globais em perfis sem permissão. Ideal: carregar os recursos necessários a cada tela, utilizando React Query.

Correspondência exata:

- `add('clients')` → `POST /api/clientes`.
- `add('buildings')` → `POST /api/predios`.
- `add('elevators')` → `POST /api/elevadores`.
- `add('users')` → `POST /api/usuarios`.
- `update('elevators', id, patch)` → `PATCH /api/elevadores/{id}`.
- `update('tickets', id, {tecnicoId})` → `PATCH /api/chamados/{id}/atribuicao`.
- `update('tickets', id, {status:'Em andamento'})` → `POST /api/chamados/{id}/iniciar`.
- `addTicket()` → `POST /api/chamados`.
- `saveChecklist()` → `POST /api/checklists`.
- Tela dashboard → `GET /api/dashboard/resumo`.
- Relatórios → `GET /api/relatorios` e `GET /api/relatorios/{id}/pdf`.

## 4. Mudanças estruturais necessárias no front

- Transformar os métodos de escrita de `AppContext` em `async` (a API pode falhar).
- Atualizar as telas que hoje assumem que `add()` retorna imediatamente um registro.
- Separar estado de sessão (`AuthContext`) de cache de dados de domínio.
- Não permitir perfis de cliente/técnico obterem dados globais por uma única requisição.
- Para download de PDF, usar `fetch(...,credentials:'include')`, `Blob` e `URL.createObjectURL`, em vez de exigir que o método de JSON processe o PDF.
- Não usar `resetDemo()` quando o modo API estiver ativo.
- Tratar erros 400, 401, 403, 409, 500 e 503, além de carregamentos.
- A tela para concluir recuperação de senha ainda precisa ser criada no React (`/reset-password?token=...`).

## 5. Contratos de checklist

Os 29 códigos em `src/data/monthlyChecklist.ts` devem permanecer idênticos aos de `MonthlyCatalog.java`. O Spring exige exatamente 29 respostas únicas de valor `S`, `N` ou `NA`, quando `tiposVisita` inclui `Mensal`, bem como assinatura do técnico e do responsável. `NA` não significa falha automática. O cliente só vê relatórios dos seus próprios prédios.

## 6. Não ativar IA ainda

A automação por IA foi planejada para depois da API convencional. Nenhum endpoint de IA consta como implementado neste backend. Não exponha a porta do Ollama ao navegador quando integrarmos a IA.
