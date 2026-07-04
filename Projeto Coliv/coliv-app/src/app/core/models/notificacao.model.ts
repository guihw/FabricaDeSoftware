export type TipoNotificacao =
  | 'NOVA_MENSAGEM'
  | 'NOVO_MATCH'
  | 'DESPESA_ADICIONADA'
  | 'PAGAMENTO_CONFIRMADO'
  | 'NOVA_AVALIACAO'
  | 'CONVITE_RECEBIDO'
  | 'CONVITE_ACEITO';

export interface Notificacao {
  id: number;
  usuarioId: number;
  tipo: TipoNotificacao;
  titulo: string;
  mensagem: string;
  referenciaId: number | null;
  lida: boolean;
  criadoEm: string;
}
