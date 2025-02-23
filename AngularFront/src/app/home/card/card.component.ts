import { Component, Input, OnInit } from '@angular/core';
import { ProductsServiceLocalStorageService } from 'src/app/Service/products-service-local-storage.service';
import { WishlistService } from 'src/app/Service/wishlist.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ProductServiceService } from 'src/app/Service/product-service.service';
import { Store } from '@ngxs/store';
@Component({
  selector: 'app-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.scss'],
})

export class CardComponent  implements OnInit{
isFavorite:boolean = false;
user_id:any;
user:any;
constructor(private store:Store,public productServiceStorage:ProductsServiceLocalStorageService, private snackBar:MatSnackBar,private wishlistService: WishlistService,private productService:ProductServiceService){
  this.user=this.store.selectSnapshot(s=>s.AuthStore?.User)
  this.user_id=this.user?.iduser;
}

@Input() products:any
@Input() productsRecommandations:any
@Input() new:boolean = true;
@Input() recommandations:boolean = true;


  toggleFavorite(id: any): void {
    this.wishlistService
      .addToWishlist({
        idUser:this.user?.iduser,
        idProduct: id,
      })
      .subscribe(
        (response: any) => {
          console.log('Added to wishlist successfully', response);
          this.snackBar.open(response.message, 'Close', {
            duration: 3000,
          });

          this.isFavorite = !this.isFavorite;
        },
        (error: any) => {
          console.error('Error adding to wishlist', error);

          this.isFavorite = !this.isFavorite;

          this.snackBar.open(
            'Failed to add the Product to your wishlist',
            'Close',
            {
              duration: 3000,
            }
          );
        }
      );
  }

  addCount(product_id:any,count:any){
    this.productService.addCount(this.user_id,product_id,count).subscribe(
      (response: any) => {
        console.log('count added successfully', response);
      },
      (error: any) => {
        console.error('Error adding to wishlist', error);
      }
    )
  }
  addProduct(product: any) {
    this.productServiceStorage.addProduct(product);
      this.snackBar.open("Product Added",'',{
        duration:2000,
      })
  }

  clear() {
    this.productServiceStorage.clearProductList();
  }

  ngOnInit(): void {}
}
