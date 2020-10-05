package com.wikipediaMatrix;

import com.wikipediaMatrix.exception.UrlInvalideException;

public class Test {
    public static void main( String[] args ){
try {
    // Url test1 = new Url(new URL(https://en.wikipedia.org/wiki/Comparison_of_dance_pad_video_games);
    System.out.println(Url.EstLangueValide("https://hj.wikipedia.org/wiki/Comparison_of_dance_pad_video_games"));


}catch (UrlInvalideException e){
     e.printStackTrace();
}
          }
}
