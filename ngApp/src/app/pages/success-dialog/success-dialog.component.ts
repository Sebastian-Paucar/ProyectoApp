import { Component } from '@angular/core';
import {MatDialogActions, MatDialogContent, MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'app-success-dialog',
  standalone: true,
  imports: [
    MatDialogContent,
    MatDialogActions
  ],
  templateUrl: './success-dialog.component.html',
  styles: ``
})
export class SuccessDialogComponent {
  constructor(public dialogRef: MatDialogRef<SuccessDialogComponent>) {}

  onClose(): void {
    this.dialogRef.close();
  }
}
