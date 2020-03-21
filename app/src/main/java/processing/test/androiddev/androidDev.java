package processing.test.androiddev;

import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import android.view.MotionEvent; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class androidDev extends PApplet {



Tic ticTacToeGame;
Pong pong;
Four four;

int game = 0;
int games = 3;
String[] gameNames;
PVector[] buttonLocations;
float buttonHeight;
public void setup() {
  
  orientation(PORTRAIT);
  buttonLocations = new PVector[games];
  for (int i = 0; i < games; i++) {
    buttonLocations[i] = new PVector (0, 0);
  }
  buttonHeight = (height - 100) / 3;
  for (int i = 0; i < games; i++) {
    PVector buttonLocation = new PVector(width/2, (i * buttonHeight) + 100 + buttonHeight / 2);
    buttonLocations[i] = buttonLocation;
  }
  gameNames = new String[]{"TICTACTOE", "PONG", "CONNECT SOME"};
}

public void draw() {
  //game is the current game 0 is menu 1 is tic tac toe
  //MENU
  if (game == 0) {
    background(0);
    noStroke();
    //rect(0, 0, width, height);
    textAlign(CENTER);
    textSize(50);
    fill(0, 255, 0);
    text("TWO-PLAYER GAMES", width/2, 50);
    for (int i = 0; i < games; i++) {
      fill(20, 255, 20);
      //DRAW BUTTON
      circle(buttonLocations[i].x, buttonLocations[i].y, buttonHeight);
      int fontSize = 25;
      textSize(fontSize);
      textAlign(CENTER);
      fill(255);
      text(gameNames[i], width/2, buttonLocations[i].y);
    }

    for (int i = 0; i < touches.length; i++) {
      float d = (100 + 100 * touches[i].area) * displayDensity;
      fill(0, 255 * touches[i].pressure);
      ellipse(touches[i].x, touches[i].y, d, d);
      fill(255, 0, 0);
      text(touches[i].id, touches[i].x + d/2, touches[i].y - d/2);
    } 
    //ADD THE BACK BUTTON HERE AS AN OVERLAY FOR ALL GAMES
  } else if ( game == 1) {
    //TIC TAC TOE
    background(255);
    strokeWeight(1);
    stroke(0);
    ticTacToeGame.draw();
  } else if ( game == 2) {
    pong.draw();
    boolean topAdjusted = false;
    boolean botAdjusted = false;
    for (int i = 0; i < touches.length; i++) {
      if (!topAdjusted) {
        if (touches[i].y < height / 2) {//control the top paddle 
          pong.moveTopPaddle(new PVector(touches[i].x, touches[i].y));
          topAdjusted = true;
        }
      }
      if (!botAdjusted) {
        if (touches[i].y > height / 2) { // control the bot paddle
          pong.moveBotPaddle(new PVector(touches[i].x, touches[i].y));
          botAdjusted = true;
        }
      }
    }
    //PONG
  } else if ( game == 3) {
    four.draw();//connect up to 4
  }
}
public void mousePressed() {
  if (game == 0) {
    PVector mouse = new PVector(mouseX, mouseY);
    for (int i = 0; i < games; i++) {
      if ((PVector.sub(mouse, buttonLocations[i]).mag()) <= buttonHeight/2) {
        game = i + 1;
        if (game == 1) {
          ticTacToeGame = new Tic();
        } else if (game == 2) {
          pong = new Pong();
        } else if (game == 3){
          four = new Four();
        }
      }
    }
  } else if (game == 1) {
    ticTacToeGame.mark();
  } else if (game == 2) {

    
  } else if (game == 3){
     int column = PApplet.parseInt(mouseX / four.squareWidth);
     four.drop(column); 
  }
}

public void backPressed() {
  if (game == 0) { 
    exit();
  } else if (game > 0) {
    game = 0;
  }
}
class Four {

  int[][] boxes;
  int turn = 0;
  int winner = -1;
  float squareWidth = width / 7;
  float squareHeight = (height - 100)/6;

  Four() {
    this.boxes = new int[6][7];
    for (int i = 0; i < boxes.length; i++) {
      for (int j = 0; j < boxes[i].length; j++) {
        this.boxes[i][j] = 0;
      }
    }
  }

  public void drop(int column) {
      boolean noSlot = true;
      int i = 5;
      while (noSlot && i >= 0) {
        if (boxes[i][column] == 0) { // empty
          noSlot = false; // exit while
        }
        i -= 1;
      }
      i += 1;
      if (!noSlot) {
        boxes[i][column] = (turn % 2) + 1; // 1 or 2
        turn += 1;
        print("drop");
      }

      
  }


    public void draw() {
      fill(200, 200, 0);
      rect(0, 0, width, height);
      stroke(1);
      line(0, height - 100, width, height - 100);
      noStroke();
      for (int i = 0; i < boxes.length; i++) {
        for (int j = 0; j < boxes[i].length; j++) {
          if (boxes[i][j] == 0) {
            fill(255, 255, 255);
            ellipse(j * squareWidth + squareWidth/2, i * squareHeight + squareHeight/2, squareWidth, squareHeight);
          } else if (boxes[i][j] == 1) {
            fill(255, 0, 0);
            ellipse(j * squareWidth + squareWidth/2, i * squareHeight + squareHeight/2, squareWidth, squareHeight);
          } else if (boxes[i][j] == 2) {
            fill(0, 0, 255);
            ellipse(j * squareWidth + squareWidth/2, i * squareHeight + squareHeight/2, squareWidth, squareHeight);
          }
        }
      }
      textAlign(CENTER);
      textSize(15);
      fill(0, 0, 0);
      if (turn % 2 == 0) {
        text("Red turn.", width/2, height - 50);
      } else if (turn % 2 == 1) {
        text("Blue turn.", width/2, height - 50);
      } else if (turn == 9) {
        text("Draw!", width/2, height - 50);
      }
    }
  
}
class Pong {
  int paddleWidth = width / 4;
  int paddleHeight = height / 12;
  //Ball
  PVector ballVel = new PVector(0, 1); // 0 x 1 y
  PVector ballLoc = new PVector(width/2, height/2); 
  int ballSize = PApplet.parseInt((height * 0.025f));
  int framerate = 30;
  int frames = 0;
  int delay = 1;
  //Paddle 1
  PVector topPaddleLoc = new PVector(width/2 - paddleWidth / 2, paddleHeight - paddleHeight / 2);
  PVector topPaddleVel = new PVector(0, 0);
  int topScore = 0;
  //Paddle 2
  PVector botPaddleLoc = new PVector(width/2 - paddleWidth / 2, height - paddleHeight * 1.5f);
  PVector botPaddleVel = new PVector(0, 0);
  int botScore = 0;

  Pong() {
    spawnBall();
  }

  public void spawnBall() {
    ballLoc = new PVector(PApplet.parseInt(width/2), PApplet.parseInt(height/2));
    int randNum = PApplet.parseInt(random(0, 2)); // rounds to 1 or 0
    if (randNum == 1) {
      ballVel = new PVector(0, -1);//goes towards top player
    } else {
      ballVel = new PVector(0, 1);//goes toward bottom player
    }
  }

  public void centerPaddles() {
  }

  public void update() { // increases difficulty with time
    ballLoc = PVector.add(ballLoc, ballVel);
    
    //WALL HIT
    if(ballLoc.x >= width - ballSize / 2){
      ballLoc.x = width - ballSize / 2;
      ballVel.x = -ballVel.x;
    }
    if(ballLoc.x <= ballSize / 2){
      ballLoc.x = ballSize/2;
      ballVel.x = -ballVel.x;
    }
    
    topPaddleLoc = PVector.add(topPaddleLoc, topPaddleVel);
    botPaddleLoc = PVector.add(botPaddleLoc, botPaddleVel);
    topPaddleVel = new PVector(0, 0);
    botPaddleVel = new PVector(0, 0);
    if (ballLoc.y > height) {
      topScore += 1;
      spawnBall();
    } 
    if (ballLoc.y < 0) {
      botScore += 1; 
      spawnBall();
    }
  }

  public void draw() {
    background(0);
    fill(255, 255, 255);
    stroke(255);
    line(0, height/2, width, height/2); //middle line
    fill(0, 255, 0);
    noStroke();
    textAlign(CENTER);
    textSize(15);
    text(topScore, width / 16, height / 2 - 3); //score
    text(botScore, width / 16, height / 2 + 15); //score
    rect(topPaddleLoc.x, topPaddleLoc.y, paddleWidth, paddleHeight); //top paddle 
    rect(botPaddleLoc.x, botPaddleLoc.y, paddleWidth, paddleHeight); //bottom paddle

    fill(255, 255, 255);
    circle(ballLoc.x, ballLoc.y, ballSize);//ball

    strokeWeight(1);

    frames += 1;
    paddleHit();
    update();
  }

  public void moveBotPaddle(PVector location) {
    //location is mouse location. paddle will approach mouse x
    if (botPaddleLoc.x < location.x) {
      botPaddleVel = new PVector(1, 0);
    } else if (botPaddleLoc.x > location.x) {
      botPaddleVel = new PVector(-1, 0);
    }
  }

  public void moveTopPaddle(PVector location) {
    if (topPaddleLoc.x < location.x) {
      topPaddleVel = new PVector(1, 0);
    } else if (topPaddleLoc.x > location.x) {
      topPaddleVel  = new PVector(-1, 0);
    }
  }

  public boolean paddleHit() {
    boolean hit = false;
    //top paddle hit
    if (ballLoc.y - ballSize / 2 == topPaddleLoc.y + paddleHeight && ballLoc.x + ballSize / 2  < topPaddleLoc.x + paddleWidth && ballLoc.x - ballSize / 2 > topPaddleLoc.x) {
      this.ballVel = new PVector(ballVel.x, -ballVel.y);
      this.ballVel = PVector.add(ballVel, topPaddleVel);
      print("tophit");
      hit = true;
      print(ballVel);
    }
    //bottom paddle hit
    if (ballLoc.y + ballSize / 2 >= botPaddleLoc.y && ballLoc.x + ballSize / 2  < botPaddleLoc.x + paddleWidth && ballLoc.x - ballSize / 2 > botPaddleLoc.x) {
      this.ballVel = new PVector(ballVel.x, -ballVel.y);
      this.ballVel = PVector.add(ballVel, botPaddleVel);
      print("bothit");
      hit = true;
      print(ballVel);
    }
    return hit;
  }
}
class Tic {

  int[][] boxes;
  int turn = 0;
  int winner = -1;
  float squareWidth = width / 3;
  float squareHeight = (height - 100)/3;
  Tic() {
    this.boxes = new int[3][3];
    for (int i = 0; i < boxes.length; i++) {
      for (int j = 0; j < boxes.length; j++) {
        this.boxes[i][j] = 0;
      }
    }
  }

  public void makeTurn() {
  }

  public boolean checkWin() {
    boolean win = false;
    boolean searching = true;
    while ( win == false && searching) {
      //check rows
      for (int i = 0; i < 3; i++) {
        int count = 0;
        int seen = -1; // -1 is just an initial value. It is changed to what is seen in the square
        for (int j = 0; j < 3; j++) {
          if (seen == -1) {
            count += 1;
            seen = boxes[i][j]; // will be box[i][0] what is in the left most square we are looking at
          } else if ( seen == 1 || seen == 2) {
            if ( seen == boxes[i][j] ) {
              count+= 1;// we have seen a reoccurance count goes up and seen stays the same.
            } else {
              seen = boxes[i][j]; // we didn't see the same as last time so new seen
            }
          } else {
          }
          if (count == 3) {
            winner = seen;
            win = true; // but who??
          }
        }
      }
      //check columns
      for (int j = 0; j < 3; j++) {
        int count = 0;
        int seen = -1; // -1 is just an initial value. It is changed to what is seen in the square
        for (int i = 0; i < 3; i++) {
          if (seen == -1) {
            count += 1;
            seen = boxes[i][j]; // will be box[i][0] what is in the left most square we are looking at
          } else if ( seen == 1 || seen == 2) {
            if ( seen == boxes[i][j] ) {
              count+= 1;// we have seen a reoccurance count goes up and seen stays the same.
            } else {
              seen = boxes[i][j]; // we didn't see the same as last time so new seen
            }
          } else {
            //nothing because 0 is blank spacem.m
          }
          if (count == 3) {
            winner = seen;              
            win = true; // but who??
          }
        }
      }
      //check diagonals

      int seen = -1;
      int count = 0;
      for (int i = 0; i < 3; i++) {
        if (seen == -1) {
          count += 1;
          seen = boxes[i][i]; // will be box[i][0] what is in the left most square we are looking at
        } else if ( seen == 1 || seen == 2) {
          if ( seen == boxes[i][i] ) {
            count+= 1;// we have seen a reoccurance count goes up and seen stays the same.
          } else {
            seen = boxes[i][i]; // we didn't see the same as last time so new seen
          }
        } else {
          //nothing because 0 is blank spacem.m
        }

        if (count == 3) {
          winner = seen;              
          win = true; // but who??
        }
      }

      seen = -1;
      count = 0;
      for (int i = 2; i >= 0; i--) {
        if (seen == -1) {
          count += 1;
          seen = boxes[i][i]; // will be box[i][0] what is in the left most square we are looking at
        } else if ( seen == 1 || seen == 2) {
          if ( seen == boxes[i][i] ) {
            count+= 1;// we have seen a reoccurance count goes up and seen stays the same.
          } else {
            seen = boxes[i][i]; // we didn't see the same as last time so new seen
          }
        } else {
          //nothing because 0 is blank spacem.m
        }

        if (count == 3) {
          print(seen); // what character was seen with the winning count.
          winner = seen;
          win = true; // but who??
        }
      }


      searching = false;
    }

    return win;
  }

  public void mark() {
    int i;
    int j;
    // based on mouseX and mouseY what block is it in?
    j = PApplet.parseInt(mouseX / squareWidth);// j
    i = PApplet.parseInt(mouseY / squareHeight);

    if ( i < 3 && j < 3 && boxes[i][j] == 0) {
      if (turn % 2 == 0) {
        boxes[i][j] = 2;
      } else {
        boxes[i][j] = 1;
      }

      turn+=1;
    }
    print("i: " + i + " j: " + j);
    print(this.checkWin());
  }

  public void draw() {
    fill(0);
    background(255);
    textAlign(LEFT);
    textSize(squareWidth + (squareHeight / 2) / 2);
    char player = ' ';

    line(squareWidth, 0, squareWidth, height - 100);
    line(squareWidth*2, 0, squareWidth*2, height - 100);
    line(0, squareHeight, width, squareHeight);
    line(0, squareHeight*2, width, squareHeight*2);

    if (turn % 2 == 0) {
      player = 'O';
    } else {
      player = 'X';
    }

    //drawing squares and their symbol
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (boxes[i][j] == 1) {
          text('X', squareWidth * j, squareHeight * (i + 1));
        }
        if (boxes[i][j] == 2) {
          text('O', squareWidth * j, squareHeight * (i + 1));
        }
      }
    }
    textSize(10);
    if (winner == 1) {
      text("X wins!", width/2, height - 50);
    } else if (winner == 2) {
      text("O wins!", width/2, height - 50);
    } else if (turn == 9) {
      text("Draw!", width/2, height - 50);
    } else {
      text(player + "'s turn", width/2, height - 50);
    }

    //backbutton
    circle(10, height + 10, 20);
  }
}
  public void settings() {  fullScreen(); }
}
