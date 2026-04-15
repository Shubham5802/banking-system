package com.banking.user_service.learningDSA;

import java.util.HashMap;

public class TwoSum {
    public static void main(String[] args) {
        int[] nums={3,2,4};
        int[] temp=new int[2];
        int  target=6;

        temp=twoSum(nums,target);
        System.out.println(temp);
        System.out.println(temp[0]+" "+temp[1]);
    }
//int[] nums={3,2,4};
    private static int[] twoSum(int[] nums, int target) {
        HashMap<Integer,Integer> map=new HashMap<>();
        for(int i=0;i< nums.length;i++){
            int temp=target-nums[i];
            if(map.containsKey(temp)){
                return new int[]{map.get(temp),i};
            }map.put(nums[i],i);
        }
        return new int[]{};
    }
}
