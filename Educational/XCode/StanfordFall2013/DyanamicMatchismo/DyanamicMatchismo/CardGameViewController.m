//
//  CardGameViewController.m
//  Matchismo
//
//  Created by Ethan Petuchowski on 11/12/13.
//  Copyright (c) 2013 Ethan Petuchowski. All rights reserved.
//

#import "CardGameViewController.h"
#import "PlayingCardDeck.h"
#import "CardMatchingGame.h" /* controller imports model */

/* the parentheses notation is just for the .m file */
@interface CardGameViewController()

@property (strong, nonatomic) CardMatchingGame *game;

#define PLAYING_CARDS_TO_MATCH 2

@end


@implementation CardGameViewController

@synthesize game = _game;

- (CardMatchingGame *)game
{
    /* using a ternary operator for this doesn't work
     * because then you never actually assign to the _inner-variable */
    if (!_game) _game = [[CardMatchingGame alloc]
                         initWithCardCount:[self.cardButtons count]
                                 usingDeck:[self createDeck]
                           numCardsToMatch:PLAYING_CARDS_TO_MATCH];
    return _game;
}


- (Deck *)createDeck
{
    return [[PlayingCardDeck alloc] init];
}


- (void)updateUI
{
    for (UIButton *cardButton in self.cardButtons) {
        int cardButtonIndex = [self.cardButtons indexOfObject:cardButton];
        Card *card = [self.game cardAtIndex:cardButtonIndex];
        [cardButton setTitle:[self titleForCard:card]
                    forState:UIControlStateNormal];
        [cardButton setBackgroundImage:[self backgroundImageForCard:card]
                              forState:UIControlStateNormal];
        cardButton.titleLabel.font = [UIFont systemFontOfSize:14];
        cardButton.enabled = !card.isMatched;
        self.scoreLabel.text =
            [NSString stringWithFormat:@"Score: %d", self.game.score];
    }
}


- (NSString *)titleForCard:(Card *)card
{
    return card.isChosen ? card.contents : @"";
}


- (UIImage *)backgroundImageForCard:(Card *)card
{
    return [UIImage imageNamed:card.isChosen ? @"cardfront" : @"cardback"];
}

@end
