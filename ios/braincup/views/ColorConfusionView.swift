//
//  ChainCalculationView.swift
//  braincup
//
//  Created by Simon Schubert on 05.10.19.
//  Copyright © 2019 Simon Schubert. All rights reserved.
//

import SwiftUI
import shared

struct ColorConfusionView: View {
    var game: ColorConfusionGame
    var answer: (String) -> Void
    var back: () -> Void
    
    var body: some View {
        NavigationView {
            VStack { Text(game.displayedShape.getChar()).foregroundColor(game.displayedColor.getColor()).font(.system(size: 100)).padding(.horizontal, 16)
                
                Text("\(game.shapePoints) = \(game.answerShape.getName())").font(.title)
                Text("\(game.colorPoints) = \(game.answerColor.getName())").font(.title).foregroundColor(game.stringColor.getColor())
                
                HStack {
                    ForEach(game.getPossibleAnswers(), id: \.hash) { number in
                        NumberPadButton(value: number, onInputChange: {value in self.answer(value)})
                    }
                }.padding(.top, 32)
            }
            .navigationBarItems(leading: Button(action: { self.back()}){Image("back").foregroundColor(Color(hex: 0xFFED7354))})
        }.navigationViewStyle(StackNavigationViewStyle()) 
    }
}
