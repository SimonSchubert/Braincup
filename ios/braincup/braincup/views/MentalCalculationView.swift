//
//  ChainCalculationView.swift
//  braincup
//
//  Created by Simon Schubert on 05.10.19.
//  Copyright © 2019 Simon Schubert. All rights reserved.
//

import SwiftUI
import shared

struct MentalCalculationView: View {
    var game: MentalCalculationGame
    var answer: (String) -> Void
    var back: () -> Void
    
    var body: some View {
        NavigationView {
            VStack {
                Text(game.calculation).font(.title).padding(.horizontal, 16)
                CalculatorView(showOperators: false, onInputChange: {value in
                    if(self.game.getNumberLength() == value.count) {
                        self.answer(value)
                    }
                })
            }
            .navigationBarItems(leading: Button(action: { self.back() }){Text("< Back")})
        }
    }
}
