import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {FavorDetailsComponent} from './favor-details/favor-details.component';
import {UserDetailsComponent} from './user-details/user-details.component';
import {RequestFavorComponent} from './request-favor/request-favor.component';


const routes: Routes = [
  { path: 'favor/:favorId', component: FavorDetailsComponent },
  { path: 'user/:userId',   component: UserDetailsComponent },
  { path: 'request-favor',  component: RequestFavorComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
