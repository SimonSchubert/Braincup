//
//  BackgroundButtonStyle.swift
//  braincup
//
//  Created by Simon Schubert on 06.10.19.
//  Copyright © 2019 Simon Schubert. All rights reserved.
//

import Foundation
import SwiftUI

struct BackgroundButtonStyle: ButtonStyle {
  func makeBody(configuration: Self.Configuration) -> some View {
    configuration.label
      .padding()
      .foregroundColor(.white)
      .background(configuration.isPressed ? Color.red : .orange)
      .cornerRadius(4)
  }
}
