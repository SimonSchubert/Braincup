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
            VStack {
                Text(game.displayedShape.getChar()).foregroundColor(game.displayedColor.getColor()).font(.system(size: 100)).padding(.horizontal, 16)
                
                Text("\(game.shapePoints) = \(game.answerShape.getName())").font(.body)
                Text("\(game.colorPoints) = \(game.answerColor.getName())").font(.body).foregroundColor(game.stringColor.getColor())
                
                CalculatorView(showOperators: false, onInputChange: {value in
                    if(self.game.points().count == value.count) {
                        self.answer(value)
                    }
                })
            }
            .navigationBarItems(leading: Button(action: { self.back() }){Text("< Back")})
        }
    }
}
