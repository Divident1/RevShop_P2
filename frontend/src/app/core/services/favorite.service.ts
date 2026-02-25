import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Favorite {
    id: number;
    buyer: any;
    product: any;
    addedAt: string;
}

@Injectable({
    providedIn: 'root'
})
export class FavoriteService {

    private API = 'http://localhost:8080/api/favorites';

    constructor(private http: HttpClient) { }

    addFavorite(buyerId: number, productId: number): Observable<Favorite> {
        return this.http.post<Favorite>(`${this.API}/${buyerId}/${productId}`, {});
    }

    removeFavorite(buyerId: number, productId: number): Observable<string> {
        return this.http.delete(`${this.API}/${buyerId}/${productId}`, { responseType: 'text' });
    }

    getFavoritesByBuyer(buyerId: number): Observable<Favorite[]> {
        return this.http.get<Favorite[]>(`${this.API}/buyer/${buyerId}`);
    }

    isFavorite(buyerId: number, productId: number): Observable<{ isFavorite: boolean }> {
        return this.http.get<{ isFavorite: boolean }>(`${this.API}/${buyerId}/${productId}/check`);
    }
}
