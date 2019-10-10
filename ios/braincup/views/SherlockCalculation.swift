//
//  ChainCalculationView.swift
//  braincup
//
//  Created by Simon Schubert on 05.10.19.
//  Copyright © 2019 Simon Schubert. All rights reserved.
//

import SwiftUI
import shared

struct SherlockCalculationView: View {
    var game: SherlockCalculationGame
    var answer: (String) -> Void
    var back: () -> Void
    
    var body: some View {
        NavigationView {
            VStack {
                Text("Goal: \(game.result)").font(.title).padding(.horizontal, 16)
                Text("Numbers: \(game.getNumbersString())").font(.subheadline).lineLimit(nil)
                CalculatorView(showOperators: true, onInputChange: {value in
                    if(self.game.isCorrect(input: value)) {
                        self.answer(value)
                    }
                })
                Button(action: {self.answer("")}) {
                    Text("Give up")
                }.buttonStyle(BackgroundButtonStyle()).padding(.top, 16)
            }
            .navigationBarItems(leading: Button(action: { self.back()}){Image("back")})
        }
    }
}
