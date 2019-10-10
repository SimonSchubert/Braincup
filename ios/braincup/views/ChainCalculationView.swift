//
//  ChainCalculationView.swift
//  braincup
//
//  Created by Simon Schubert on 05.10.19.
//  Copyright © 2019 Simon Schubert. All rights reserved.
//

import SwiftUI
import shared

struct ChainCalculationView: View {
    var game: ChainCalculationGame
    var answer: (String) -> Void
    var back: () -> Void
    
    var body: some View {
        NavigationView {
            VStack {
                Text(game.calculation).font(.title).padding(.horizontal, 16)
                CalculatorView(showOperators: false, onInputChange: {value in
                    if(self.game.isCorrect(input: value)) {
                        self.answer(value)
                    }
                })
                Button(action: {
                    self.answer("")
                }) {
                    Text("Give up")
                }.buttonStyle(BackgroundButtonStyle()).padding(.top, 16)
            }
            .navigationBarItems(leading: Button(action: { self.back()}){Image("back")})
        }
    }
}
