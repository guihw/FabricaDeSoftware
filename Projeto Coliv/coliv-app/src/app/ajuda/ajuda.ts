import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { TopNavbarComponent } from '../shared/components/top-navbar-component/top-navbar-component';
import { AuthService } from '../core/services/auth.service';
import { inject } from '@angular/core';

interface FaqItem {
  pergunta: string;
  resposta: string;
}

interface FaqSecao {
  icone: string;
  titulo: string;
  itens: FaqItem[];
}

@Component({
  selector: 'app-ajuda',
  standalone: true,
  imports: [CommonModule, RouterLink, TopNavbarComponent],
  templateUrl: './ajuda.html',
  styleUrl: './ajuda.css',
})
export class Ajuda {
  private auth = inject(AuthService);

  get estaLogado(): boolean {
    return !!this.auth.getUserId();
  }

  get feedLink(): string {
    return this.auth.getUserType() === 'anfitriao' ? '/feedanfitriao' : '/feedcolega';
  }

  aberto = signal<string | null>(null);

  toggle(id: string): void {
    this.aberto.update(atual => (atual === id ? null : id));
  }

  secoes: FaqSecao[] = [
    {
      icone: 'info',
      titulo: 'Sobre o Coliv',
      itens: [
        {
          pergunta: 'O que é o Coliv?',
          resposta:
            'O Coliv é uma plataforma de moradia compartilhada que conecta pessoas que buscam um lugar para morar (Colegas) a pessoas que disponibilizam espaços (Anfitriões). Mais do que um simples aluguel, o Coliv cuida da compatibilidade entre os moradores para criar comunidades saudáveis e funcionais.',
        },
        {
          pergunta: 'O que está incluso no aluguel?',
          resposta:
            'Isso varia por anúncio, mas na maioria das unidades Coliv estão inclusos: internet de alta velocidade, condomínio e uso das áreas comuns. Cada anúncio detalha exatamente o que está coberto.',
        },
        {
          pergunta: 'Como funciona a curadoria de moradores?',
          resposta:
            'Ao se cadastrar, todos os usuários passam por uma verificação de identidade e antecedentes. Além disso, as preferências de convivência são cruzadas para sugerir compatibilidades mais assertivas entre Colegas e Anfitriões.',
        },
      ],
    },
    {
      icone: 'manage_accounts',
      titulo: 'Conta e Cadastro',
      itens: [
        {
          pergunta: 'Como criar uma conta?',
          resposta:
            'Acesse a página inicial e clique em "Criar Conta". Escolha seu perfil (Colega ou Anfitrião), preencha seus dados e siga o passo a passo. Após o cadastro, você será direcionado para configurar suas preferências de convivência.',
        },
        {
          pergunta: 'Esqueci minha senha, o que faço?',
          resposta:
            'Na tela de login, clique em "Esqueci minha senha" e informe o e-mail cadastrado. Você receberá um link para redefinir sua senha em instantes. Verifique também a caixa de spam.',
        },
        {
          pergunta: 'Como alterar meus dados de perfil?',
          resposta:
            'Acesse a seção "Perfil" pelo menu inferior e clique em "Editar". Você pode atualizar seu nome e e-mail. Salve as alterações ao final.',
        },
        {
          pergunta: 'Como excluir minha conta?',
          resposta:
            'Na página de Perfil, role até o final e clique em "Excluir conta". Atenção: esta ação é permanente e remove todos os seus dados da plataforma, incluindo anúncios e histórico de conversas.',
        },
      ],
    },
    {
      icone: 'search',
      titulo: 'Para Colegas',
      itens: [
        {
          pergunta: 'Como encontrar uma moradia?',
          resposta:
            'Após o cadastro e configuração das suas preferências, acesse o Feed. Lá você verá anúncios de moradias compatíveis com o seu perfil. Clique em um card para ver os detalhes completos do espaço e do anfitrião.',
        },
        {
          pergunta: 'Como demonstrar interesse em uma moradia?',
          resposta:
            'No detalhe do anúncio, clique em "Tenho interesse". O anfitrião receberá sua solicitação e poderá aceitar ou recusar. Se aceito, um chat será aberto para vocês se comunicarem diretamente.',
        },
        {
          pergunta: 'Posso conversar com o anfitrião antes de fechar?',
          resposta:
            'Sim! Assim que o anfitrião aceitar seu interesse, um canal de chat é aberto entre vocês. Use o chat para tirar dúvidas, combinar visitas e alinhar todos os detalhes antes de tomar uma decisão.',
        },
      ],
    },
    {
      icone: 'key',
      titulo: 'Para Anfitriões',
      itens: [
        {
          pergunta: 'Como anunciar minha moradia?',
          resposta:
            'Acesse o menu superior e clique em "Criar Anúncio". Preencha as informações do espaço (fotos, descrição, localização, preço e o que está incluso). Após publicar, seu anúncio ficará visível no feed de Colegas compatíveis.',
        },
        {
          pergunta: 'Como gerenciar meus anúncios?',
          resposta:
            'Acesse "Gerenciar Anúncios" no menu. Lá você pode editar, pausar ou excluir seus anúncios a qualquer momento. Anúncios pausados ficam invisíveis no feed, mas seus dados são mantidos.',
        },
        {
          pergunta: 'Como selecionar um colega?',
          resposta:
            'No feed de anfitriões, você verá os perfis de Colegas interessados no seu anúncio. Analise o perfil, preferências e histórico de cada um e escolha com quem deseja iniciar uma conversa. Após aceitar, o chat será aberto automaticamente.',
        },
      ],
    },
  ];
}
