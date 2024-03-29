import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppComponent} from './app.component';
import {HeaderComponent} from './header/header.component';
import {FooterComponent} from './footer/footer.component';
import {AppRoutingModule} from './app-routing.module';
import {LoginComponent} from './login/login.component';
import {FullScreenComponent} from './full-screen/full-screen.component';
import {SignupComponent} from './signup/signup.component';
import {ProfileComponent} from './profile/profile.component';
import {HomeComponent} from './home/home.component';
import {NotFoundComponent} from './not-found/not-found.component';
import {authInterceptorProviders} from "./_helpers/auth.interceptor";
import {HttpClientModule} from "@angular/common/http";
import {ReactiveFormsModule} from "@angular/forms";
import {TemplatesComponent} from './templates/templates.component';
import {SizeFormatterPipe} from './_helpers/size-formatter.pipe';
import {xhrInterceptorProviders} from "./_helpers/xhr.interceptor";
import {NotificationComponent} from './notification/notification.component';
import {NgOptimizedImage} from "@angular/common";
import {HighlightModule} from "ngx-highlightjs";
import {highlightsProvider} from "./_helpers/hightlights";

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    LoginComponent,
    FullScreenComponent,
    SignupComponent,
    ProfileComponent,
    HomeComponent,
    NotFoundComponent,
    TemplatesComponent,
    SizeFormatterPipe,
    NotificationComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule,
    NgOptimizedImage,
    HighlightModule
  ],
  providers: [authInterceptorProviders, xhrInterceptorProviders, highlightsProvider],
  bootstrap: [AppComponent]
})
export class AppModule { }
