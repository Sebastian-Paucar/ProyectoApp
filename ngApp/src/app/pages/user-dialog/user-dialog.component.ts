import {Component, Inject, OnInit} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatButton} from "@angular/material/button";
import {ChatService} from "../../services/chat.service";
import {ChatMessage} from "../../models/ChatMessage";
import {FormsModule} from "@angular/forms";
import {NgClass, NgForOf} from "@angular/common";


@Component({
  selector: 'app-user-dialog',
  standalone: true,
  imports: [
    MatDialogActions,
    MatButton,
    MatDialogContent,
    MatDialogTitle,
    FormsModule,
    NgClass,
    NgForOf
  ],
  templateUrl: './user-dialog.component.html',
  styleUrls: ['./user-dialog.component.scss']
})
export class UserDialogComponent implements  OnInit{

  messageInput: string = '';
  userId: string="";
  messageList: any[] = [];
  username: string | undefined;
  token: string | null = null;
  chatroom!:string;
  constructor(
    public dialogRef: MatDialogRef<UserDialogComponent>,
    private chatService: ChatService,
    @Inject(MAT_DIALOG_DATA) public data: { name: string, sender: string },

  ) {}
  ngOnInit(): void {
    this.userId = this.data.name;
    this.chatService.initConnenctionSocket();
    this.chatroom=this.combinerEntrants(this.data.name,this.data.sender);
    console.log(this.chatroom);
    this.chatService.joinRoom(this.chatroom);
    this.lisenerMessage();
  }
  sendMessage() {
    const chatMessage = {
      message: this.messageInput,
      user: this.userId
    }as ChatMessage
    this.chatService.sendMessage(this.chatroom, chatMessage);
    this.messageInput = '';
  }
  combinerEntrants(a: string, b: string): string {
    const entradas = [a, b];
    entradas.sort();
    return entradas.join('');
  }

  lisenerMessage() {
    this.chatService.getMessageSubject().subscribe((messages: any) => {
      this.messageList = messages.map((item: any)=> ({
        ...item,
        message_side: item.user === this.userId ? 'sender': 'receiver'
      }))
    });
  }
  closeDialog(): void {
    this.dialogRef.close();
  }


}
