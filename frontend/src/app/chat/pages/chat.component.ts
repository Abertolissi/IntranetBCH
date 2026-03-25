import { Component } from '@angular/core';

interface ConversationItem {
  id: number;
  title: string;
  preview: string;
  time: string;
  active?: boolean;
}

interface MessageItem {
  role: 'assistant' | 'user';
  content: string;
  timestamp: string;
}

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent {
  prompt = '';

  conversations: ConversationItem[] = [
    {
      id: 1,
      title: 'Resumen semanal de riesgos',
      preview: 'Genera puntos clave para comite ejecutivo.',
      time: 'Hoy, 09:42',
      active: true
    },
    {
      id: 2,
      title: 'Respuesta a auditoria interna',
      preview: 'Necesito una respuesta formal en 5 bullets.',
      time: 'Ayer, 18:11'
    },
    {
      id: 3,
      title: 'Borrador comunicado regional',
      preview: 'Adapta tono para publico no tecnico.',
      time: 'Lun, 12:33'
    }
  ];

  messages: MessageItem[] = [
    {
      role: 'assistant',
      content:
        'Hola, soy tu agente de chat para la intranet. Puedo ayudarte a resumir politicas, redactar comunicados y estructurar respuestas para auditoria.',
      timestamp: '09:42'
    },
    {
      role: 'user',
      content:
        'Necesito un resumen ejecutivo del avance mensual del area de operaciones con enfoque en riesgos y mitigaciones.',
      timestamp: '09:43'
    },
    {
      role: 'assistant',
      content:
        'Perfecto. Te propongo estructurarlo en tres bloques: estado actual, riesgos prioritarios y plan de mitigacion. Si quieres, te genero una version de 7 lineas para direccion y otra extendida para jefaturas.',
      timestamp: '09:43'
    }
  ];

  selectConversation(item: ConversationItem): void {
    this.conversations = this.conversations.map((conversation) => ({
      ...conversation,
      active: conversation.id === item.id
    }));
  }

  sendPrompt(): void {
    const value = this.prompt.trim();
    if (!value) {
      return;
    }

    this.messages = [
      ...this.messages,
      {
        role: 'user',
        content: value,
        timestamp: this.currentTime()
      },
      {
        role: 'assistant',
        content:
          'Recibido. Estoy preparando una respuesta con formato ejecutivo y accionable basada en tu solicitud.',
        timestamp: this.currentTime()
      }
    ];

    this.prompt = '';
  }

  private currentTime(): string {
    return new Date().toLocaleTimeString('es-PE', {
      hour: '2-digit',
      minute: '2-digit',
      hour12: false
    });
  }
}
