import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent implements OnInit {

  resetForm!: FormGroup;

  message='';
  error='';

  constructor(private fb:FormBuilder,
              private authService:AuthService){}

  ngOnInit(){

    this.resetForm=this.fb.group({

      email:['',[Validators.required,Validators.email]],

      newPassword:['',Validators.required],

      confirmPassword:['',Validators.required]

    });

  }

  resetPassword(){

    if(this.resetForm.invalid){

      this.resetForm.markAllAsTouched();
      return;

    }

    const email=this.resetForm.value.email;
    const newPassword=this.resetForm.value.newPassword;
    const confirmPassword=this.resetForm.value.confirmPassword;

    if(newPassword!==confirmPassword){

      this.error="Passwords do not match";
      this.message='';
      return;

    }

    const payload={
      email:email,
      newPassword:newPassword
    };

    this.authService.resetPassword(payload)
    .subscribe({

      next:(res:any)=>{

        this.message=res;
        this.error='';

      },

      error: (err: any) => {

  console.log(err);

  if (err.error && typeof err.error === 'string') {
    this.error = err.error;
  } 
  else if (err.error && err.error.message) {
    this.error = err.error.message;
  } 
  else {
    this.error = "Something went wrong. Please try again.";
  }

  this.message = '';

}

    });

  }

}