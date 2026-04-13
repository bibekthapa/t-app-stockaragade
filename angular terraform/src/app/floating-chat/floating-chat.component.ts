import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChatComponent } from "../chat/chat.component";

@Component({
  selector: 'app-floating-chat',
  standalone : true ,
  imports: [CommonModule, ChatComponent],
  templateUrl: './floating-chat.component.html',
  styleUrl: './floating-chat.component.css'
})
export class FloatingChatComponent {

  isOpen: boolean = false;

  toggleChat() : void{
    this.isOpen = !this.isOpen;
  }

}
