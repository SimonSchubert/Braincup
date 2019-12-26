//
//  PathFinderView.swift
//  braincup
//
//  Created by Simon Schubert on 26.12.19.
//  Copyright © 2019 Simon Schubert. All rights reserved.
//

import SwiftUI
import shared

struct PathFinderView: View {
    var game: PathFinderGame
    var answer: (String) -> Void
    var back: () -> Void
    var gridSize: Int
    let blankFigure = Figure.init(shape: shared.Shape.square, color: shared.Color.greyLight, rotation: 0)
    let startFigure = Figure.init(shape: shared.Shape.square, color: shared.Color.orange, rotation: 0)
    
    var body: some View {
        NavigationView {
            VStack {
                ForEach((game.directions as! [Direction]).chunked(into: gridSize + 2), id: \.self) { directions in
                    HStack {
                        ForEach(directions, id: \.self) { direction in
                            direction.getFigure().draw(size: 32)
                        }
                    }
                }
                ForEach((0...self.gridSize), id: \.self) { y in
                    HStack {
                        ForEach((0...self.gridSize), id: \.self) { x in
                            Button(action: { self.answer("\(x+1) \(y+1)") }) {
                                self.blankFigure.draw(size: 48, isMarked: x == self.game.startX && y == self.game.startY)
                            }
                        }
                    }
                }
            }.navigationBarItems(leading: Button(action: { self.back()}){Image("back").foregroundColor(Color(hex: 0xFFED7354))})
        }.navigationViewStyle(StackNavigationViewStyle())
    }
}
