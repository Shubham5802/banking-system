package com.banking.user_service.learningDSA;

public class Pattern {
    public static void main(String[] args) {
        pattern(4);
    }
    public static void pattern(int n){
        for(int i=1;i<=n;i++){
            for(int j=1;j<=n;j++){
                System.out.print(i);
            }
            System.out.println();
        }
    }
}
