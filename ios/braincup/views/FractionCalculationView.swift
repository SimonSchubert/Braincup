//
//  ChainCalculationView.swift
//  braincup
//
//  Created by Simon Schubert on 20.10.19.
//  Copyright © 2019 Simon Schubert. All rights reserved.
//

import SwiftUI
import shared

struct FractionCalculationView: View {
    var game: FractionCalculationGame
    var answer: (String) -> Void
    var back: () -> Void
    
    var body: some View {
        NavigationView {
            VStack {
                Text(game.calculation).font(.title).padding(.horizontal, 16).frame(minWidth: 0, maxWidth: .infinity)
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
            .navigationBarItems(leading: Button(action: { self.back()}){Image("back").foregroundColor(Color(hex: 0xFFED7354))})
        }.navigationViewStyle(StackNavigationViewStyle())
    }
}
