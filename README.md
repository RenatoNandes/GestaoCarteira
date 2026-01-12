# Sistema de Gestão de Carteiras Financeiras

## 📌 Descrição do Projeto
Este projeto consiste no desenvolvimento de um **Sistema de Gerenciamento de Carteiras de Investimentos**, implementado em **Java**, com foco na aplicação correta dos **conceitos de Programação Orientada a Objetos (POO)**.

O sistema permite o cadastro, edição, exclusão e consulta de **ativos financeiros**, **investidores** e **movimentações de compra e venda**, além do acompanhamento detalhado da composição e do valor das carteiras de investimento.

A aplicação funciona via **interface em console** e realiza carregamento e manipulação de dados em memória, conforme especificação do trabalho.

---

## 👥 Integrantes do Grupo
- Lucas Gonçalves Dahbar - 202476028
- Renato de Souza Nandes - 202476043

---

## 🎯 Objetivos
- Aplicar corretamente os conceitos de **herança, abstração, interfaces e encapsulamento**
- Desenvolver um sistema robusto com **tratamento completo de exceções**
- Modelar um domínio realista do mercado financeiro
- Implementar operações de investimento respeitando regras de negócio
- Gerar relatórios estruturados em **JSON ou YAML**

---

## 🧩 Funcionalidades Implementadas

### ✔️ Ativos
- Carregamento inicial de ativos a partir de arquivos CSV
- Cadastro individual de ativos
- Cadastro de ativos em lote
- Edição de ativos existentes
- Exclusão de ativos com propagação para as carteiras
- Relatórios de ativos:
  - Todos os ativos
  - Apenas Ações
  - Apenas FIIs
  - Apenas Criptoativos
  - Apenas Stocks
  - Apenas Tesouro

---

### ✔️ Investidores
- Cadastro de investidores pessoa física e institucionais
- Cadastro de investidores em lote
- Listagem de investidores cadastrados
- Exclusão de investidores por CPF ou CNPJ
- Seleção de investidor para operações específicas

---

### ✔️ Carteira de Investimentos
- Registro de movimentações de compra e venda
- Controle da quantidade de ativos (quantidade real)
- Cálculo automático:
  - Valor total gasto
  - Valor total atual
  - Percentual de renda fixa e renda variável
  - Percentual de ativos nacionais e internacionais
- Validação das regras de investimento conforme o perfil do investidor
- Bloqueio de vendas acima da quantidade disponível

---

### ✔️ Movimentações
- Cadastro de movimentações individuais
- Cadastro de movimentações em lote
- Validação da existência do ativo antes da negociação
- Registro de:
  - Tipo de negociação (compra ou venda)
  - Instituição financeira
  - Quantidade negociada
  - Preço de execução
  - Data da negociação

---

### ✔️ Relatórios
- Geração de relatório completo do investidor selecionado
- Formato: **JSON ou YAML**
- O relatório contém:
  - Lista de ativos da carteira
  - Quantidade por ativo
  - Valor total investido
  - Valor total atual
  - Percentuais consolidados
  - Totais gerais

---

## 🧠 Conceitos de Orientação a Objetos Aplicados
- **Herança**: especialização de tipos de ativos e investidores
- **Abstração**: uso de classes abstratas para ativos
- **Interfaces**: definição de comportamentos comuns
- **Encapsulamento**: atributos privados com acesso controlado
- **Polimorfismo**: comportamento específico conforme o tipo do ativo

---

## ⚠️ Tratamento de Exceções
- Uso de exceções personalizadas para regras de negócio
- Utilização de exceções nativas do Java sempre que aplicável
- Validação rigorosa de dados de entrada
- Mensagens claras e informativas exibidas no console

---

## 📂 Arquivos de Teste
Foram criados arquivos CSV específicos para testar:
- Inserção em lote de ativos
- Inserção em lote de investidores
- Inserção em lote de movimentações

Esses arquivos permitem validar corretamente as funcionalidades exigidas no trabalho.

---

## 📊 Diagrama de Classes UML
O projeto possui um **Diagrama de Classes UML**, incluído no relatório final, representando fielmente a modelagem da solução proposta e a relação entre as classes.

## 📝 Observações Finais
O projeto foi desenvolvido seguindo rigorosamente a especificação fornecida, priorizando **organização do código**, **boas práticas de POO**, **tratamento adequado de exceções** e **clareza na interação via console**.
