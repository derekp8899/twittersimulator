#include <stdio.h>
#include <stdlib.h>
#include <time.h>

struct bot{
  
  int follows;
  int lastRT;
  int views;
  
};

int main(int argc, char * argv){

  struct bot **head = malloc(sizeof(struct bot*)*10);
  int i = 0;
  srand(time(0));//seed rng
  //init the bots
  for(i = 0; i < 10; i++){

    struct bot *temp = malloc(sizeof(struct bot));
    temp->follows = i;
    temp->lastRT = 0;
    temp->views = 0;
    head[i] = temp;
      

  }
  //  head[6]->follows = 45;
  for( i = 0; i < 10; i++){

    printf("bot %d follows %d\n",i,head[i]->follows);

  }//verify init follower counts
  int stopCond = 1000;
  int totFollows = 10;
  int numMessages = 0;//number of messages sent
  while(totFollows < stopCond){//loop to continue new tweets until stop condition is reached

    int newViews  = rand() % totFollows;//random number of views max is total followers this should eventually be drawn from weibull dist
    int newFollows = newViews * .15;//new views for now is 15% rounded of new views
    for( i = 0; i < 10; i++){

      head[i]->follows+=newFollows;
      
    }
    totFollows += newFollows;
    numMessages++;
  }
  printf("goal of %d followers reached after %d messages\n\n",stopCond,numMessages);
}
