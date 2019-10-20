//
//  ChainCalculationView.swift
//  braincup
//
//  Created by Simon Schubert on 20.10.19.
//  Copyright © 2019 Simon Schubert. All rights reserved.
//

import SwiftUI
import shared

struct HeightComparisonView: View {
    var game: HeightComparisonGame
    var answer: (String) -> Void
    var back: () -> Void
    
    var body: some View {
        NavigationView {
            VStack {
                ForEach(0..<game.answers.count, id: \.self) { i in
                    Button(action: {self.answer("\(i+1)")}) {
                        Text(self.game.answers[i] as! String)
                    }.buttonStyle(BackgroundButtonStyle()).padding(.top, 16)
                }
            }
            .navigationBarItems(leading: Button(action: { self.back()}){Image("back").foregroundColor(Color(hex: 0xFFED7354))})
        }.navigationViewStyle(StackNavigationViewStyle())
    }
}
