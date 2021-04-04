import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'cart',
        data: { pageTitle: 'coopcycleApp.cart.home.title' },
        loadChildren: () => import('./cart/cart.module').then(m => m.CartModule),
      },
      {
        path: 'order',
        data: { pageTitle: 'coopcycleApp.order.home.title' },
        loadChildren: () => import('./order/order.module').then(m => m.OrderModule),
      },
      {
        path: 'payment-option',
        data: { pageTitle: 'coopcycleApp.paymentOption.home.title' },
        loadChildren: () => import('./payment-option/payment-option.module').then(m => m.PaymentOptionModule),
      },
      {
        path: 'user-account',
        data: { pageTitle: 'coopcycleApp.userAccount.home.title' },
        loadChildren: () => import('./user-account/user-account.module').then(m => m.UserAccountModule),
      },
      {
        path: 'cooperative',
        data: { pageTitle: 'coopcycleApp.cooperative.home.title' },
        loadChildren: () => import('./cooperative/cooperative.module').then(m => m.CooperativeModule),
      },
      {
        path: 'product',
        data: { pageTitle: 'coopcycleApp.product.home.title' },
        loadChildren: () => import('./product/product.module').then(m => m.ProductModule),
      },
      {
        path: 'institution',
        data: { pageTitle: 'coopcycleApp.institution.home.title' },
        loadChildren: () => import('./institution/institution.module').then(m => m.InstitutionModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
