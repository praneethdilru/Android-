package com.theempires.jetshop.manage;

import com.theempires.jetshop.model.Item;

import java.util.ArrayList;
import java.util.List;

    public class CartManager {
        private static List<Item> cartItems = new ArrayList<>();

        public static List<Item> getCartItems() {
            return cartItems;
        }

        public static void addToCart(String userId, Item item) {
            cartItems.add(item);
        }


    }

