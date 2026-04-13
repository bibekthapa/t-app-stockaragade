import { Component ,OnInit } from '@angular/core';
import { ChaterviceService } from '../services/chatervice.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

interface Message{
  text : string ,
  sender: 'user' | 'bot';
}

@Component({
  selector: 'app-chat',
  imports: [FormsModule,CommonModule],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.css'
})
export class ChatComponent {

  userInput = '';
  messages: Message[] = [] ;

  ngOnInit():void{
    this.loadMessages();
  }

  constructor(private chatService : ChaterviceService){}

  sendMessage(){
      if(!this.userInput.trim()) return ;

      this.messages.push({text:this.userInput , sender : 'user'});
      const messageToSend = this.userInput;
      this.userInput = '' ; 
      this.saveMessages();

      this.chatService.sendMessage(messageToSend).subscribe(
        {
          next : (res) => {
            this.messages.push({text: res.reply , sender : 'bot'});
            this.saveMessages();
        },
          error : (err) => {
            this.messages.push({text : 'Error contacting server',sender : 'bot'});
            this.saveMessages();
            console.error(err);
          } 
        });

  }

    saveMessages() : void {
      localStorage.setItem('chatMessage',JSON.stringify(this.messages));
    }

    loadMessages(): void{
      const saved = localStorage.getItem('chatMessage');
      if(saved){
        this.messages = JSON.parse(saved);
      }
    }

  
}
