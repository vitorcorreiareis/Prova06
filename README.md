# ImobiControl

Aplicativo Android para gestão imobiliária completa, desenvolvido com Jetpack Compose e Firebase.

## Funcionalidades

- **Autenticação** — Cadastro, login e recuperação de senha via Firebase Auth
- **Imóveis** — Cadastro, edição e listagem de imóveis com busca de endereço pelo CEP (ViaCEP)
- **Locações** — Gestão de contratos de locação com controle de status
- **Financeiro** — Registro e acompanhamento de pagamentos por locação
- **Manutenção** — Abertura e acompanhamento de chamados de manutenção
- **Documentos** — Upload e visualização de documentos por imóvel
- **Perfil** — Edição dos dados do usuário

## Tecnologias

| Camada | Tecnologia |
|--------|-----------|
| UI | Jetpack Compose + Material 3 |
| Navegação | Jetpack Navigation Compose |
| Arquitetura | MVVM + Repository Pattern |
| Backend | Firebase Firestore + Firebase Auth |
| API externa | Retrofit 2 + ViaCEP |
| Linguagem | Kotlin |
| Build | Gradle (Kotlin DSL) |

## Estrutura do Projeto

```
app/src/main/java/com/example/prova06/
├── data/
│   ├── model/          # Data classes (Property, Rental, Payment, ...)
│   ├── remote/         # Retrofit + ViaCEP
│   └── repository/     # Repositórios Firebase
├── navigation/         # NavGraph e rotas
├── ui/
│   ├── auth/           # Login, Cadastro, Recuperação de senha
│   ├── dashboard/      # Tela inicial com resumo
│   ├── properties/     # Gestão de imóveis
│   ├── rentals/        # Gestão de locações
│   ├── finance/        # Controle financeiro
│   ├── maintenance/    # Chamados de manutenção
│   ├── documents/      # Documentos
│   ├── profile/        # Perfil do usuário
│   ├── components/     # Componentes reutilizáveis
│   └── theme/          # Cores, tipografia e tema
└── utils/              # Extensions, validações, constantes
```

## Configuração

### Pré-requisitos

- Android Studio Hedgehog ou superior
- JDK 17
- Conta Firebase

### Instalação

1. Clone o repositório:
   ```bash
   git clone https://github.com/vitorcorreiareis/Prova06.git
   ```

2. Crie um projeto no [Firebase Console](https://console.firebase.google.com) e ative:
   - Authentication (Email/Senha)
   - Cloud Firestore

3. Baixe o arquivo `google-services.json` e coloque em `app/`

4. Abra o projeto no Android Studio e sincronize o Gradle

5. Execute o app em um emulador ou dispositivo físico

## Requisitos mínimos

- Android 8.0 (API 26)
- Conexão com internet

## Licença

Este projeto foi desenvolvido para fins acadêmicos.
