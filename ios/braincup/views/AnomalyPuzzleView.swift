//
//  AnomalyPuzzleView.swift
//  braincup
//
//  Created by Simon Schubert on 26.12.19.
//  Copyright © 2019 Simon Schubert. All rights reserved.
//

import SwiftUI
import shared

struct AnomalyPuzzleView: View {
    var game: AnomalyPuzzleGame
    var answer: (String) -> Void
    var back: () -> Void
    var chunkSize: Int
    
    var body: some View {
        NavigationView {
            VStack {
                ForEach((game.figures as! [Figure]).chunked(into: chunkSize), id: \.self) { figures in
                    HStack {
                        ForEach(figures, id: \.self) { figure in
                            Button(action: { self.answer("\(self.game.figures.index(of: figure)+1)") }) {
                                figure.draw(size: 64)
                            }
                        }
                    }
                }
            }.navigationBarItems(leading: Button(action: { self.back()}){Image("back").foregroundColor(Color(hex: 0xFFED7354))})
        }.navigationViewStyle(StackNavigationViewStyle())
    }
}
